<template>
    <div>
        <div class="chat-item" v-for="(item, index) in relationChatListData"
             @contextmenu.prevent="menuPanel($event, item.id)"
             :key="index"
             @click="startChat($event, item.id, item.name, item.picture, item.data)">
            <div class="img"><img :src="item.picture" alt="NO IMG"></div>
            <div class="info" v-if="item.data.type === '0'"
                 v-html="'<div class=nick><div>' + item.name + '</div></div>' + item.lastMsg">
            </div>
            <div class="info" v-if="item.data.type === '1'"
                 v-html="'<div class=nick>' + item.name + '</div>' + item.data.user + '：' + item.lastMsg">
            </div>
            <!-- 新消息提示 -->
            <span class="msg-tips">
                <el-badge :value="item.unRead" class="item"></el-badge>
            </span>
        </div>

        <!-- 右键面板 -->
        <div class="cover" @contextmenu.prevent="" ref="coverHook" @click="closePanel"></div>
        <div class="right-click-panel" ref="clickPanel">
            <ul class="panel-list">
                <li @click="readMsg">
                    <i class="el-icon-success"></i>
                    标为已读
                </li>
                <li @click="noneReadMsg">
                    <i class="el-icon-info"></i>
                    标为未读
                </li>
                <li @click="deleteRelationChat">
                    <i class="el-icon-remove"></i>
                    从最近联系人中移除
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
    import {getUser} from "../../utils/auth";
    import chatApi from "../../api/chat";
    import relationChatApi from "../../api/relation_chat";
    import "../../assets/css/msg-tips.css"

    export default {
        props: {
            "relationChatListData": {
                type: Array,
                default: [],
            },
        },
        data() {
            return {
                currentId: 0,
                currentDom: null,
            };
        },
        methods: {
            startChat(event, currentId, nick, avatar, data) {
                let currentDom = event.currentTarget;
                this.$store.dispatch("friend/setCurrentNick", nick);
                this.$store.dispatch("friend/setCurrentAvatar", avatar);
                this.$store.dispatch("friend/setCurrentId", currentId);
                // 设置发送类型
                this.$store.dispatch("chat/setSendType", data.type);
                // 判断用户还是群
                if (data.type === '0') {
                    // 获取当前用户的id
                    let currentUserId = getUser().id;
                    chatApi.getChatRecord(currentUserId, currentId).then((response) => {
                        let data = response.data.data;
                        // 排序聊天记录
                        data.sort((c1, c2) => {
                            return c1.id - c2.id;
                        });
                        // 设置聊天记录
                        this.$store.dispatch("friend/setChatRecord", data);
                    });
                } else {
                    chatApi.getGroupChatRecord(currentId).then((response) => {
                        let data = response.data.data;
                        // 排序聊天记录
                        data.sort((c1, c2) => {
                            return c1.sortId - c2.sortId;
                        });
                        // 设置聊天记录
                        this.$store.dispatch("friend/setGroupChatRecord", data);
                    });
                }

                /**
                 * 删除redis上的未读记录
                 */
                chatApi.removeUnRead(getUser().id, currentId).then((response) => {
                    if (response.data.flag) {
                        // 删除消息提醒
                        currentDom.lastChild.firstChild.firstChild.innerHTML = "";
                        currentDom.lastChild.firstChild.firstChild.style.display = "none";
                    }
                });
            },
            // 显示右侧面盘
            menuPanel(event, currentId) {
                // 获取鼠标的坐标
                let top = event.clientY;
                let left = event.clientX;
                let panelDom = this.$refs.clickPanel;
                panelDom.style.top = `${top}px`;
                panelDom.style.left = `${left}px`;
                panelDom.style.display = "block";
                this.$refs.coverHook.style.display = "block";
                // 设置当前操作的好友id
                this.currentId = currentId;
                this.currentDom = event.currentTarget;
            },
            // 关闭右键面板
            closePanel() {
                this.$refs.clickPanel.style.display = "none";
                this.$refs.coverHook.style.display = "none";
            },
            // 消息已读
            readMsg() {
                this.closePanel();
                $(this.currentDom).children(".msg-tips").remove();
            },
            // 标为未读
            noneReadMsg() {
                this.closePanel();
                if ($(this.currentDom).children(".msg-tips").length === 0) {
                    let tipsStr = '<span class="msg-tips">1</span>';
                    $(this.currentDom).append(tipsStr);
                }
            },
            // 删除最近联系人
            deleteRelationChat() {
                this.closePanel();
                relationChatApi.deleteRelationChat(getUser().id, this.currentId).then((response) => {
                    this.$notify({
                        title: (response.data.flag ? "成功" : "失败"),
                        message: response.data.message,
                        type: (response.data.flag ? "success" : "error"),
                    });
                    if (response.data.flag) {
                        // 删除当前的聊天
                        $(this.currentDom).remove();
                    }
                });
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
</style>
