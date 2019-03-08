<template>
    <div>
        <div class="chat-item" v-for="(item, index) in relationChatListData"
             :key="index"
             @click="startChat(item.id, item.name, item.picture, item.data)">
            <div class="img"><img :src="item.picture" alt="NO IMG"></div>
            <div class="info" v-if="item.data.type === 0">
                <div class="nick">
                    <div>{{ item.name }}</div>
                </div>
                {{ item.lastMsg }}
            </div>
            <div class="info" v-if="item.data.type === 1">
                <div class="nick">{{ item.name }}</div>
                {{ item.data.user }}：{{ item.lastMsg }}
            </div>
            <!-- 新消息提示 -->
            <span class="msg-tips">5</span>
        </div>
    </div>
</template>

<script>
    import {getUser} from "../../utils/auth";
    import chatApi from "../../api/chat";

    export default {
        props: {
            "relationChatListData": {
                type: Array,
                default: [],
            },
        },
        methods: {
            startChat(currentId, nick, avatar, data) {
                this.$store.dispatch("friend/setCurrentNick", nick);
                this.$store.dispatch("friend/setCurrentAvatar", avatar);
                this.$store.dispatch("friend/setCurrentId", currentId);
                // 设置发送类型
                this.$store.dispatch("chat/setSendType", data.type);
                // 判断用户还是群
                if (data.type === 0) {
                    // 获取当前用户的id
                    let currentUserId = getUser().id;
                    chatApi.getChatRecord(currentUserId, currentId).then((response) => {
                        let data = response.data.data;
                        // 传送聊天记录
                        this.$store.dispatch("friend/setChatRecord", data);
                    });
                } else {
                    chatApi.getGroupChatRecord(currentId).then((response) => {
                        // 设置聊天记录
                        this.$store.dispatch("friend/setChatRecord", response.data.data);
                    });
                }
            },
        },
    }
</script>

<style scoped lang="stylus">
    .chat-item
        position relative
        display flex
        padding-left 10px

        &:hover
            background-color #f3f0e9

        .img
            flex 0 0 50px
            width 50px
            height 50px

            img
                margin 5px
                width 40px
                height 40px
                border-radius 12px

        .info
            width 0
            flex 1
            padding-left 10px
            height 50px
            text-overflow ellipsis
            line-height 25px
            white-space nowrap
            overflow hidden
            border-bottom 1px solid #dedede
            color #545454
            cursor pointer

            .nick
                line-height 25px


        .msg-tips
            position absolute
            top 8px
            right 10px
            padding 0 4px
            cursor default
            line-height 16px
            border-radius 50%
            background-color #f00
            color #fff
            font-size 12px
</style>
