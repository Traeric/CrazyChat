<template>
    <div class="wrap">
        <!-- 快捷链接 -->
        <div class="quick-link" @click="quickLink('/dynamic_space')">
            <i class="el-icon-star-off"></i>
            动态空间
        </div>
        <div class="quick-link" @click="quickLink('/chat_room')">
            <i class="el-icon-location"></i>
            聊天室
        </div>
        <div class="quick-link" @click="quickLink('/home_page')">
            <i class="el-icon-menu"></i>
            个人首页
        </div>
        <div class="quick-link" @click="quickLink('/system_notice')">
            <i class="el-icon-bell"></i>
            系统通知
        </div>

        <div class="dynamic-box">
            <div class="top">
                <div class="img">
                    <img :src="userAvatar" alt="NO IMG">
                </div>
                <div class="nick">{{ userName }}</div>
                <div class="only">
                    <el-switch
                        v-model="onlyme"
                        active-text="仅自己可见">
                    </el-switch>
                </div>
                <div style="clear: both;"></div>
            </div>
            <div class="middle">
                <div class="text" contenteditable="true" @focus="startEdit" ref="dynamic_hook" @blur="leaveEdit">说点什么吧
                </div>
                <div class="picture">
                    <div class="display-area">
                        <div class="tips">
                            <i class="el-icon-picture"></i>
                            图片（{{ imgNum }}/9）
                        </div>

                        <el-upload
                            action="https://jsonplaceholder.typicode.com/posts/"
                            list-type="picture-card"
                            :on-preview="handlePictureCardPreview"
                            :on-remove="handleRemove"
                            :on-change="onChange"
                            :on-exceed="outSize"
                            :limit="9" :auto-upload="false">
                            <i class="el-icon-plus"></i>
                        </el-upload>
                        <el-dialog :visible.sync="dialogVisible" size="tiny">
                            <img width="100%" :src="dialogImageUrl" alt="NO IMG">
                        </el-dialog>
                    </div>
                </div>
            </div>
            <div class="footer">
                <div class="see-icon" @click="checkFriend">
                    <span>
                        <i class="el-icon-view"></i>
                        对谁可见
                    </span>
                </div>
                <div class="see-area">
                    <el-tag closable v-for="(friend, index) in friendMember" :key="index"
                            :type="styleList[index % 5]"
                            style="margin: 5px 5px;"
                            @close="removeFriend(friend.id)">
                        {{ friend.todo }}
                    </el-tag>
                </div>
                <div class="send-btn">
                    <el-button type="info" plain icon="el-icon-error" @click="closePage()">关闭</el-button>
                    <el-button type="success" plain icon="el-icon-upload" @click="publishDynamic">发布动态</el-button>
                </div>
            </div>
        </div>
        <!-- 背景 -->
        <div class="bg"></div>
        <!-- 用户好友列表展示 -->
        <el-dialog title="请选择要添加的好友" width="30%" :visible.sync="friendListDialogVisible">
            <div class="friend-list">
                <div class="friend-group" v-for="(item, key, index) in friendObj" :key="index">
                    <div class="topic clear-float" @click="pullList($event)">
                        <div class="text">{{ key }}</div>
                        <i class="el-icon-arrow-right"></i>
                    </div>
                    <div class="list">
                        <div class="friend-item hover-style"
                             :friendid="friend.id"
                             v-for="(friend, index1) in item" :key="index1"
                             @click="addMember($event, friend.id)">
                            <div class="img">
                                <img :src="friend.avatar" alt="NO IMG">
                            </div>
                            <div class="text">{{ friend.todo }}</div>
                        </div>
                    </div>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="friendListDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="friendListDialogVisible = false">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import {getUser} from "../../utils/auth";
    import chatApi from '../../api/chat';
    import dynamicApi from '../../api/dynamic';


    export default {
        data() {
            return {
                onlyme: false,
                userName: "",
                userAvatar: "",
                userId: "",
                // 照片上传
                dialogImageUrl: '',
                dialogVisible: false,
                imageData: null,
                imgNum: 0,
                // 用户好友列表
                friendMemberId: [],
                friendMember: [],
                friendListDialogVisible: false,
                friendObj: {},
                friendList: {},
                styleList: ["", "success", "info", "warning", "danger"],
            };
        },
        mounted() {
            // 判断登录
            if (!getUser().id) {
                // 未登录
                this.$router.push("/login");
            }
            // 初始化用户信息
            this.userName = getUser().nick;
            this.userAvatar = getUser().avatar;
            this.userId = getUser().id;
            // 初始化上传信息
            this.imageData = new FormData();
            // 请求用户好友列表
            chatApi.getUserFriendList(getUser().id).then((response) => {
                // 将所有的好友移动到一个列表中
                let friendList = {};
                for (let friend in response.data.data) {
                    response.data.data[friend + ''].forEach((item) => {
                        friendList[item.id] = item;
                    });
                }
                this.friendObj = response.data.data;
                this.friendList = friendList;
            });
        },
        methods: {
            // 关闭按钮
            closePage() {
                history.back();
            },
            // 开始编辑动态
            startEdit() {
                let content = this.$refs.dynamic_hook.innerHTML;
                if (content.trim() !== '说点什么吧') {
                    return;
                }
                // 清空动态内容提示
                this.$refs.dynamic_hook.innerHTML = '';
            },
            // 离开编辑
            leaveEdit() {
                let content = this.$refs.dynamic_hook.innerHTML;
                if (content.trim() !== '') {
                    return;
                }
                this.$refs.dynamic_hook.innerHTML = '说点什么吧';
            },
            /*
            图片上传
             */
            handleRemove(file, fileList) {
                console.log(file, fileList);
            },
            handlePictureCardPreview(file) {
                this.dialogImageUrl = file.url;
                this.dialogVisible = true;
            },
            onChange(file) {
                // 检查文件大小
                if (file.size > 1024 * 1024 * 10) {
                    this.$message.error("图片大小超过10MB!");
                    return false;
                }
                this.imageData.append("images", file, file.name);
                this.imgNum++;
            },
            outSize() {
                this.$message.error("最多只能上传9张图片！");
            },
            // 挑选谁能查看
            checkFriend() {
                if (this.onlyme) {
                    this.$message.error("已选择仅对自己可见！");
                    return;
                }
                this.friendListDialogVisible = true;
            },
            // 下拉用户列表
            pullList(event) {
                // 添加active属性
                let groupDom = $(event.currentTarget).parent();
                if (groupDom.attr("class").indexOf("active") !== -1) {
                    // 移除active
                    groupDom.removeClass("active");
                    groupDom.children(".list").css("height", "0");
                } else {
                    // 添加active
                    groupDom.addClass("active");
                    let count = groupDom.children(".list").children().length;
                    groupDom.children(".list").css("height", `${count * 47}px`);
                }
            },
            // 选中好友
            addMember(event, friendId) {
                if ($(event.currentTarget).attr("class").indexOf("friend-active") !== -1) {
                    // 已经被选中了
                    $(event.currentTarget).removeClass("friend-active");
                    $(event.currentTarget).addClass("hover-style");
                    this.friendMemberId.splice(this.friendMemberId.indexOf(friendId), 1);
                    this.friendMember.splice(this.friendMemberId.indexOf(friendId), 1);
                } else {
                    // 还没有被选中
                    $(event.currentTarget).addClass("friend-active");
                    $(event.currentTarget).removeClass("hover-style");
                    if (this.friendMemberId.indexOf(friendId) === -1) {
                        this.friendMemberId.push(friendId);
                        this.friendMember.push(this.friendList[friendId]);
                    }
                }
            },
            // 删除选中好友
            removeFriend(friendId) {
                this.friendMemberId.splice(this.friendMemberId.indexOf(friendId), 1);
                this.friendMember.splice(this.friendMemberId.indexOf(friendId), 1);
                // 删除掉选中样式
                $(`.friend-item[friendid=${friendId}]`).removeClass("friend-active");
            },
            // 发布动态
            publishDynamic() {
                // 检查是否符合发布条件
                let content = this.$refs.dynamic_hook.innerHTML.trim();
                if (content === "" || content === "说点什么吧") {
                    this.$message.error("填写点内容吧！");
                    return;
                }
                // 添加需要传的内容
                this.imageData.append("content", content);
                this.imageData.append("friendList", this.friendMemberId);
                this.imageData.append("onlyme", this.onlyme);
                this.imageData.append("userId", this.userId);
                dynamicApi.addDynamic(this.imageData).then((response) => {
                    if (response.data.flag) {
                        this.$confirm('动态发布成功！', '成功', {
                            confirmButtonText: '发布成功，去动态空间看看吧。',
                            cancelButtonText: '还不过瘾？继续编辑吧！',
                            type: 'success',
                            center: true
                        }).then(() => {
                            // 去动态空间
                            this.$router.push("/");
                        }).catch(() => {
                            // 继续编辑
                            location.reload();
                        });
                    }
                });
            },
            // 快捷键跳转
            quickLink(link) {
                this.$router.push(link);
            },
        },
        watch: {
            onlyme() {
                if (this.onlyme) {
                    this.friendMemberId.forEach((item) => {
                        // 删除掉选中样式
                        $(`.friend-item[friendid=${item}]`).removeClass("friend-active");
                    });
                    this.friendMemberId = [];
                    this.friendMember = [];
                }
            },
        },
    }
</script>

<style scoped lang="stylus">
    .clear-float::after
        content ''
        display block
        clear both

    .wrap
        .quick-link
            position fixed
            left 20px
            width 230px
            height 100px
            line-height 100px
            text-align center
            font-size 20px
            font-weight bolder
            color #fff
            text-shadow 2px 2px 6px #636363
            border-radius 12px
            cursor pointer
            box-shadow 6px 6px 12px #919191

            &:nth-child(1)
                top 30px
                background-color #67C23A

                &:hover
                    top 34px
                    left 24px
                    box-shadow none
                    background-color #59a833

            &:nth-child(2)
                top 170px
                background-color #E6A23C

                &:hover
                    top 174px
                    left 24px
                    box-shadow none
                    background-color #c98e35

            &:nth-child(3)
                top 310px
                background-color #F56C6C

                &:hover
                    top 314px
                    left 24px
                    box-shadow none
                    background-color #d55e5e

            &:nth-child(4)
                top 440px
                background-color #909399

                &:hover
                    top 444px
                    left 24px
                    box-shadow none
                    background-color #808288

        .dynamic-box
            width 700px
            background-color #fff
            margin 50px auto
            border-radius 4px

            .top
                .img
                    float left
                    width 60px
                    height 60px

                    img
                        margin 5px
                        width 50px
                        height 50px
                        border-radius 12px

                .nick
                    float left
                    margin-left 10px
                    height 60px
                    text-align center
                    line-height 60px

                .only
                    float right
                    margin-right 20px
                    height 60px
                    line-height 60px

            .middle
                margin-top 10px

                .text
                    margin 0 2px
                    padding 10px
                    height 200px
                    border 1px solid #dedede
                    outline none

                .picture
                    .display-area
                        padding-left 42px

                        .tips
                            margin-left -30px
                            height 30px
                            line-height 30px

            .footer
                .see-icon
                    padding-right 10px
                    height 30px
                    line-height 30px
                    text-align right

                    span
                        background-color #dae0ef
                        padding 5px
                        border-radius 30px
                        cursor pointer
                        color #121212

                        &:hover
                            background-color #ccd2e0

                .see-area
                    padding 5px 30px

                .send-btn
                    padding-right 20px
                    height 70px
                    line-height 70px
                    text-align right

        .bg
            position fixed
            top 0
            bottom 0
            left 0
            right 0
            z-index -100
            background-image url("../../assets/images/dynamic/bg1.jpg")

        .friend-list
            margin 0 auto;
            width 50%

            .friend-group
                user-select none

                .topic
                    padding-left 10px
                    border-radius 8px
                    font-weight 700
                    height 40px
                    line-height 40px
                    border-bottom 1px solid #dedede
                    cursor pointer

                    &:hover
                        background-color #f9f9f9

                    .text
                        float left

                    i
                        display block
                        margin-left 5px
                        height 40px
                        line-height 40px
                        font-weight 700
                        float left

                .list
                    height 0
                    overflow hidden
                    transition height .5s ease

                    .friend-item
                        display flex
                        margin 0 0 0 10px
                        cursor pointer
                        border-radius 8px

                        &.hover-style:hover
                            background-color #f9f9f9

                        .img
                            flex 0 0 46px
                            width 46px
                            height 46px

                            img
                                margin 3px
                                border-radius 12px
                                height 40px
                                width 40px

                        .text
                            flex 1
                            padding-left 10px
                            border-bottom 1px solid #dedede
                            line-height 46px

                    .friend-active
                        background-color #98d0a6

            .active
                .topic
                    background-color #eee

                    i
                        transform rotate(90deg)
</style>
