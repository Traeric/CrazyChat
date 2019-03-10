<template>
    <div>
        <ul class="friend-list">
            <li v-for="(item, key, index) in friendList" :key="index">
                <div class="title" @click="showFriend($event)">
                    <div class="zw-arrow">
                        <i class="iconfont icon-jiantou"></i>
                    </div>
                    <div class="msg">{{ key }}</div>
                </div>
                <div class="friend-area">
                    <div class="friend-item" v-for="(friend, index) in item" :key="index"
                         @click="startChat(friend.id, friend.todo, friend.avatar)"
                         @contextmenu.prevent="menuPanel($event, friend.id, key)">
                        <div class="img"><img :src="friend.avatar" alt="NO IMG"></div>
                        <div class="name">{{ friend.todo }}</div>
                    </div>
                </div>
            </li>
        </ul>
        <!-- 右键面板 -->
        <div class="cover" @contextmenu.prevent="" ref="coverHook" @click="closePanel"></div>
        <div class="right-click-panel" ref="clickPanel">
            <ul class="panel-list">
                <li @click="showFriendInfo">
                    <i class="iconfont icon-solid-person"></i>
                    好友信息
                </li>
                <li @click="setGroup">
                    <i class="glyphicon glyphicon-tags"></i>
                    设置分组
                </li>
                <li @click="showFriendInfo">
                    <i class="glyphicon glyphicon-leaf"></i>
                    修改备注
                </li>
                <li @click="deleteFriend">
                    <i class="el-icon-remove"></i>
                    删除好友
                </li>
            </ul>
        </div>

        <!-- 分组 -->
        <el-dialog title="修改分组" :visible.sync="changeGroupDialog" width="20%">
            <div class="group-title">请选择要将该好友移动到以下哪个分组下</div>
            <el-select class="select" v-model="currentGroupName">
                <el-option :label="item.name" :value="item.id"
                           v-for="(item, index) in $store.state.user.userGroup" :key="index"></el-option>
            </el-select>
            <span slot="footer" class="dialog-footer">
                <el-button @click="changeGroupDialog = false">取 消</el-button>
                <el-button type="primary" @click="changeGroup">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import {getUser} from "../../utils/auth";
    import chatApi from "../../api/chat";
    import userApi from "../../api/user";
    import friendApi from "../../api/friend";

    export default {
        props: {
            "friendList": {
                type: Object,
                default: {},
            },
        },
        data() {
            return {
                currentFriendId: 0,
                currentFriendDom: null,
                changeGroupDialog: false,
                currentGroupName: "",
            };
        },
        methods: {
            showFriend(event) {
                if ($(event.currentTarget).parent().get(0).className === "active") {
                    $(event.currentTarget).parent().removeClass("active");
                    $(event.currentTarget).parent().children(".friend-area").css("height", `0`);
                } else {
                    $(event.currentTarget).parent().addClass("active");
                    // 设置friend-area的高度
                    let count = $(event.currentTarget).parent().children(".friend-area").children(".friend-item").length;

                    $(event.currentTarget).parent().children(".friend-area").css("height", `${count * 46}px`);
                }
            },
            startChat(friendId, nick, avatar) {
                // 获取当前用户的id
                let currentUserId = getUser().id;
                // 通过用户id和当前好友的id查询聊天记录跟用户信息
                // 将当前朋友的头像以及备注传过去
                this.$store.dispatch("friend/setCurrentNick", nick);
                this.$store.dispatch("friend/setCurrentAvatar", avatar);
                this.$store.dispatch("friend/setCurrentId", friendId);
                // 设置发送类型
                this.$store.dispatch("chat/setSendType", 0);
                chatApi.getChatRecord(currentUserId, friendId).then((response) => {
                    let data = response.data.data;
                    // 传送聊天记录
                    this.$store.dispatch("friend/setChatRecord", data);
                });

                // 回到浏览器顶部
            },
            // 右键菜单面板
            menuPanel(event, friendId, groupName) {
                // 获取鼠标的坐标
                let top = event.clientY;
                let left = event.clientX;
                let panelDom = this.$refs.clickPanel;
                panelDom.style.top = `${top}px`;
                panelDom.style.left = `${left}px`;
                panelDom.style.display = "block";
                this.$refs.coverHook.style.display = "block";
                // 设置当前操作的好友id
                this.currentFriendId = friendId;
                this.currentFriendDom = event.currentTarget;
                this.currentGroupName = groupName;
            },
            // 展示好友信息
            showFriendInfo() {
                this.closePanel();
                // 查询好友信息
                userApi.getUserInfo(this.currentFriendId).then((response) => {
                    let data = response.data.data;
                    // 计算年纪
                    let now = Date.parse(new Date());
                    let birth = Date.parse(data.birthday);
                    data["age"] = Math.floor(parseFloat((now - birth) / (1000 * 60 * 60 * 24 * 365)));
                    this.$store.dispatch("user/setpersonnalInfoDialog", true);
                    // 设置给store
                    this.$store.dispatch("user/setUserInfo", data);
                });
            },
            // 删除好友
            deleteFriend() {
                this.closePanel();
                // 确认删除
                this.$confirm('此操作将永久删除该好友, 是否继续?', '删除好友', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'error'
                }).then(() => {
                    friendApi.deleteFriend(getUser().id, this.currentFriendId).then((response) => {
                        this.$notify({
                            title: (response.data.flag ? "成功" : "失败"),
                            message: response.data.message,
                            type: (response.data.flag ? "success" : "error"),
                        });
                        if (response.data.flag) {
                            // 删除成功，移除该好友
                            $(this.currentFriendDom).remove();
                        }
                    });
                });
            },
            // 关闭右键面板
            closePanel() {
                this.$refs.clickPanel.style.display = "none";
                this.$refs.coverHook.style.display = "none";
            },
            // 设置好友分组
            setGroup() {
                this.closePanel();
                this.changeGroupDialog = true;
                // 查询全部分组
                userApi.getGroupList(getUser().id).then((response) => {
                    this.$store.dispatch("user/setUserGroup", response.data.data);
                });
            },
            // 修改分组
            changeGroup() {
                alert(this.currentGroupName);
            },
        },
    }
</script>

<style scoped lang="stylus">
    .friend-list
        border-top 1px solid #dedede

        li
            .title
                height 50px
                line-height 50px
                padding-left 10px
                color #fff
                background-color #86dffc
                border-bottom 1px solid #73b7de
                user-select none
                cursor pointer

                &:hover
                    background-color #5ec7e6

                .zw-arrow
                    display inline-block
                    width 20px

                .msg
                    display inline-block

            .friend-area
                height 0
                overflow hidden
                transition height .5s ease

                .friend-item
                    height 46px
                    padding 0 10px
                    display flex
                    background-color #fff
                    cursor pointer

                    &:hover
                        background-color #f9f9f9

                    .img
                        float 0 0 46px
                        width 46px
                        height 46px

                        img
                            margin 3px
                            width 40px
                            height 40px
                            border-radius 12px

                    .name
                        flex 1
                        margin-left 10px
                        line-height 46px
                        border-bottom 1px solid #dedede

        .active
            .title
                background-color #5ec7e6

                .zw-arrow
                    transform rotate(90deg) !important

    .cover
        position fixed
        top 0
        bottom 0
        left 0
        right 0
        display none
        z-index 100

    .right-click-panel
        position fixed
        z-index 1000
        border-radius 4px
        overflow hidden
        width 160px
        margin-right 0 !important
        border 1px solid #dedede
        box-shadow 2px 2px 6px #a3a3a3
        display none

        .panel-list
            margin-bottom 0 !important

            li
                padding-left 10px
                background-color #fff
                height 35px
                line-height 35px
                border-bottom 1px solid #e2e2e2
                cursor pointer

                &:hover
                    background-color #f1f1f1

                &:last-child
                    color #f00

    .group-title
        margin-bottom 20px
        height 30px
        line-height 30px
        font-weight 700
        font-size 18px
        text-align center

    .select
        margin-left 10px

</style>
