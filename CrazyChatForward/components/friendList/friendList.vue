<template>
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
                     @click="startChat(friend.id, friend.todo, friend.avatar)">
                    <div class="img"><img :src="friend.avatar" alt="NO IMG"></div>
                    <div class="name">{{ friend.todo }}</div>
                </div>
            </div>
        </li>
    </ul>
</template>

<script>
    import {getUser} from "../../utils/auth";
    import chatApi from "../../api/chat";

    export default {
        props: {
            "friendList": {
                type: Object,
                default: {},
            },
        },
        data() {
            return {};
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
            }
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

</style>
