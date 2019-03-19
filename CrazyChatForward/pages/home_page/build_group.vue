<template>
    <div id="wrap">
        <div class="nav">
            <div class="left col-md-2 col-lg-2 col-sm-2">
                <nuxt-link class="back" to="/home_page">
                    <i class="el-icon-back"></i>
                    返回
                </nuxt-link>
            </div>
            <div class="middle col-md-8 col-lg-8 col-sm-8">
                创建群聊
            </div>
            <div class="right col-md-2 col-lg-2 col-sm-2"></div>
        </div>
        <div class="body">
            <div class="left col-md-3 col-lg-3 col-sm-3"></div>
            <div class="middle col-md-6 col-lg-6 col-sm-6">
                <el-form ref="form" label-width="80px">
                    <el-form-item label="群聊名称" :required="true">
                        <el-input v-model="groupMap.groupName"></el-input>
                    </el-form-item>

                    <div class="groupMembers">
                        <div class="title clear-float">
                            <div class="text">邀请好友</div>
                            <div class="button">
                                <el-button type="success" @click="displayFriendList">
                                    添加<i class="el-icon-circle-plus el-icon--right"></i>
                                </el-button>
                            </div>
                            <div class="line"></div>
                        </div>
                        <!-- 展示群成员 -->
                        <div class="show-area clear-float" ref="showAreaHook" @click="deleteMember($event)">
                            <div class="member-item" :title="userInfo.nick">
                                <img :src="userInfo.avatar" alt="NO IMG">
                            </div>
                            <span class="add-span" ref="otherMemberHook"></span>
                        </div>
                    </div>

                    <el-form-item style="float: right;">
                        <el-button @click="cancel">取消</el-button>
                        <el-button type="primary" @click="createGroup">立即创建</el-button>
                    </el-form-item>
                </el-form>
            </div>
            <div class="right col-md-3 col-lg-3 col-sm-3"></div>
        </div>
        <div class="bg"></div>

        <!-- 用户好友列表展示 -->
        <el-dialog title="请选择要添加的用户" width="30%" :visible.sync="dialogVisible">
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
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="confirmPull">确 定</el-button>
            </span>
        </el-dialog>

    </div>
</template>

<script>
    import "../../assets/css/addgroup.css"
    import chatApi from '../../api/chat';
    import {getUser} from "../../utils/auth";
    import groupApi from "../../api/group";

    export default {
        data() {
            return {
                groupMap: {
                    groupName: "",
                    createTime: null,
                    createrId: "",
                },
                groupMembers: [],
                // 用户好友列表
                dialogVisible: false,
                friendObj: {},
                friendList: [],
                userInfo: {},
            };
        },
        created() {
            // 判断登录
            if (!getUser().id) {
                // 未登录
                this.$router.push("/login");
            }

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
                this.userInfo = getUser();
            });
        },
        methods: {
            cancel() {
                window.history.back();
            },
            // 展示用户的好友列表
            displayFriendList() {
                this.dialogVisible = true;
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
            // 拉人进群
            addMember(event, friendId) {
                if ($(event.currentTarget).attr("class").indexOf("friend-active") !== -1) {
                    // 已经被选中了
                    $(event.currentTarget).removeClass("friend-active");
                    $(event.currentTarget).addClass("hover-style");
                    this.groupMembers.splice(this.groupMembers.indexOf(friendId), 1);
                } else {
                    // 还没有被选中
                    $(event.currentTarget).addClass("friend-active");
                    $(event.currentTarget).removeClass("hover-style");
                    if (this.groupMembers.indexOf(friendId) === -1) {
                        this.groupMembers.push(friendId);
                    }
                }
            },
            // 确定拉人进群
            confirmPull() {
                this.dialogVisible = false;
                // 生成好友列表
                let resultStr = '';
                // 循环生成列表
                this.groupMembers.forEach((item) => {
                    let currentFriend = this.friendList[item];
                    resultStr += `
                        <div class="member-item" title="${currentFriend.todo}">
                            <img src="${currentFriend.avatar}" alt="NO IMG">
                            <div class="cancel">
                                <i class="el-icon-circle-close" data-memberid="${currentFriend.id}"></i>
                            </div>
                        </div>
                    `;
                });
                // 添加
                this.$refs.otherMemberHook.innerHTML = resultStr;

            },
            // 删除群成员
            deleteMember(event) {
                let currentDom = event.target || event.srcElement;
                if (currentDom.localName === "i") {
                    // 获取当前成员的id
                    let currentId = currentDom.dataset.memberid;
                    // 在数组中删除
                    this.groupMembers.splice(this.groupMembers.indexOf(currentId), 1);
                    // 删除掉当前的头像
                    $(currentDom).parent().parent().remove();
                    // 删除掉选中样式
                    $(`.friend-item[friendid=${currentId}]`).removeClass("friend-active");
                }
            },
            // 创建群聊
            createGroup() {
                // 检查群名是否为空
                if (this.groupMap.groupName.trim() === "") {
                    this.$message({
                        message: '群名称不能为空',
                        type: 'info'
                    });
                    return;
                }
                this.groupMap.createTime = new Date();
                this.groupMap.groupMembers = this.groupMembers;
                this.groupMap.createrId = getUser().id;
                // 展示加载动画
                const loading = this.$loading({
                    lock: true,
                    text: '正在创建群聊。。。',
                    spinner: 'el-icon-loading',
                    background: 'rgba(255, 255, 255, 0.8)'
                });
                // 发送请求，开始创建
                groupApi.createGroup(this.groupMap).then((response) => {
                    this.$notify({
                        title: (response.data.flag ? "成功" : "失败"),
                        message: response.data.message,
                        type: (response.data.flag ? "success" : "error"),
                    });
                    loading.close();
                    if (response.data.flag) {
                        this.$router.push("/home_page");
                    }
                });
            },
        },
    }
</script>

<style scoped lang="stylus">
    .clear-float::after
        content ""
        display block
        clear both

    #wrap
        .bg
            position fixed
            top 0
            bottom 0
            left 0
            right 0
            background-color #f5f5f5
            z-index -1

        .nav
            padding-left 10px
            height 60px

            .left
                height 100%
                line-height 60px

                a.back
                    display block
                    text-decoration none
                    color #85d5fc
                    font-weight 700
                    font-size 20px

                    &:hover
                        color #5bc0de

            .middle
                height 100%
                line-height 60px
                font-weight 700
                font-size 24px
                color #fff
                text-shadow 2px 2px 6px #595959
                text-align center

        .body
            margin-top 30px

            .middle
                .groupMembers
                    .title
                        .text
                            float left
                            font-weight 700
                            font-size 18px
                            line-height 40px

                        .button
                            float right

                        .line
                            float left
                            margin 10px 20px
                            box-sizing border-box
                            width 95%
                            border-bottom 1px solid #cfcfcf

                    .show-area
                        padding 10px 2px
                        margin 20px 0
                        border 1px solid #dedede
                        border-radius 8px
                        box-shadow 2px 2px 6px #a7a7a7

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
