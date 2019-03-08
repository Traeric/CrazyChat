<template>
    <ul class="group-list">
        <li v-for="(item, index) in groupList" :key="index" @click="startChat(item.id, item.name, item.picture)">
            <div class="img"><img :src="item.picture" alt="NO IMG"></div>
            <div class="text">
                <div class="name">{{ item.name }}</div>
                {{ item.lastMsg.user }}：{{ item.lastMsg.msg }}
            </div>
        </li>
    </ul>
</template>

<script>
    import chatApi from '../../api/chat';

    export default {
        props: {
            "groupList": {
                type: Array,
                default: [],
            },
        },
        methods: {
            startChat(groupId, name, picture) {
                // 显示群的名字以及头像
                this.$store.dispatch("friend/setCurrentNick", name);
                this.$store.dispatch("friend/setCurrentAvatar", picture);
                this.$store.dispatch("friend/setCurrentId", groupId);
                // 设置发送类型
                this.$store.dispatch("chat/setSendType", 1);
                // 获取聊天记录
                chatApi.getGroupChatRecord(groupId).then((response) => {
                    // 设置聊天记录
                    this.$store.dispatch("friend/setChatRecord", response.data.data);
                });
            },
        },
    }
</script>

<style scoped lang="stylus">
    .group-list
        border-top 1px solid #dedede
        li
            display flex
            padding 3px 10px
            cursor pointer
            background-color #fffcf5
            &:hover
                background-color #f3f0e9

            .img
                flex 0 0 46px
                width 46px
                height 46px

                img
                    margin 3px
                    width 40px
                    height 40px
                    border-radius 50%

            .text
                flex 1
                width 0
                text-overflow ellipsis
                overflow hidden
                white-space nowrap
                padding-left 10px
                line-height 23px
                color #666
                border-bottom 1px solid #dedede
                .name
                    color #000
</style>
