<template>
    <div>
        <ul class="group-list">
            <li class="hover" v-for="(item, index) in groupList" :key="index"
                @click="startChat(item.id, item.name, item.picture)"
                @contextmenu.prevent="iJoinedMenuPanel($event, item.id)"
                v-if="item.type === '0'">
                <div class="img"><img :src="item.picture" alt="NO IMG"></div>
                <div class="text">
                    {{ item.name }}
                </div>
            </li>
            <li class="i-created">我创建的群</li>
            <li class="hover" v-for="(item, index) in groupList" :key="index"
                @click="startChat(item.id, item.name, item.picture)"
                @contextmenu.prevent="iCreatedMenuPanel($event, item.id)"
                v-if="item.type === '1'">
                <div class="img"><img :src="item.picture" alt="NO IMG"></div>
                <div class="text">
                    {{ item.name }}
                </div>
            </li>
        </ul>

        <div class="cover" @contextmenu.prevent="" ref="coverHook" @click="closePanel"></div>
        <!-- 我加入的群 -->
        <div class="right-click-panel" ref="joinClickPanel">
            <ul class="panel-list">
                <li @click="groupInfo">
                    <i class="iconfont icon-qunzu2"></i>
                    群信息
                </li>
                <li @click="groupMember('0')">
                    <i class="iconfont icon-solid-person"></i>
                    群成员
                </li>
                <li @click="quitGroup">
                    <i class="glyphicon glyphicon-log-out"></i>
                    退出群聊
                </li>
            </ul>
        </div>
        <!-- 我创建的群 -->
        <div class="right-click-panel" ref="createClickPanel">
            <ul class="panel-list">
                <li @click="groupInfo">
                    <i class="iconfont icon-qunzu2"></i>
                    群信息
                </li>
                <li @click="groupMember('1')">
                    <i class="iconfont icon-solid-person"></i>
                    群成员
                </li>
                <li @click="dissolutionGroup">
                    <i class="el-icon-circle-close"></i>
                    解散群聊
                </li>
            </ul>
        </div>

        <!-- Dialog显示区域 -->
        <!-- 群信息 -->
        <el-dialog title="群聊信息" width="30%" :visible.sync="groupInfoDialog">
            <div class="group-picture"><img :src="groupInfoObj.picture" alt="NO IMG"></div>
            <div class="group-info-item">
                <div class="left">群名：</div>
                <div class="right">{{ groupInfoObj.name }}</div>
            </div>
            <div class="group-info-item">
                <div class="left">创建时间：</div>
                <div class="right">{{ groupInfoObj.create_time }}</div>
            </div>
            <div class="group-info-item">
                <div class="left">群主：</div>
                <div class="right">
                    <div class="img"><img :src="groupMaster.avatar" alt="NO IMG"></div>
                    <div class="name">
                        <nuxt-link to="">
                            {{ groupMaster.nick }}
                        </nuxt-link>
                    </div>
                </div>
            </div>
        </el-dialog>
        <!-- 群成员 -->
        <el-dialog title="群聊成员" width="20%" :visible.sync="groupMemberDialog">
            <div class="member clear-float" v-for="(item, index) in groupMembers" :key="index">
                <div class="img"><img :src="item.avatar" alt="NO IMG"></div>
                <div class="name">
                    {{ item.nick }}
                </div>
                <el-tag type="warning" v-if="item.type === '1'" style="margin-top: 8px;">群主</el-tag>
                <div class="del-button" v-if="currentGroupType === '1'">
                    <el-button type="danger" size="small" icon="el-icon-remove" style="float: right;"
                               @click="removeMember($event, item.id)" v-if="item.type === '0'">移出群聊
                    </el-button>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import chatApi from '../../api/chat';
    import groupApi from "../../api/group";
    import userApi from '../../api/user';
    import {getUser} from "../../utils/auth";

    export default {
        props: {
            "groupList": {
                type: Array,
                default: [],
            },
        },
        data() {
            return {
                currentGroupId: 0,
                groupInfoObj: {},
                groupMaster: {},
                groupInfoDialog: false,
                groupMemberDialog: false,
                groupMembers: [],
                currentGroupType: "0",   // 是我加入的群还是我创建的群
                currentGroupDom: null,
            };
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
                    this.$store.dispatch("friend/setGroupChatRecord", response.data.data);
                });
            },
            // 我加入的群
            iJoinedMenuPanel(event, groupId) {
                // 获取鼠标位置
                let top = event.clientY;
                let left = event.clientX;
                // 设置面板
                this.$refs.joinClickPanel.style.top = `${top}px`;
                this.$refs.joinClickPanel.style.left = `${left}px`;
                this.$refs.joinClickPanel.style.display = "block";
                this.$refs.coverHook.style.display = "block";
                this.currentGroupId = groupId;
                this.currentGroupDom = event.currentTarget;
            },
            // 我创建的群聊
            iCreatedMenuPanel(event, groupId) {
                // 获取鼠标位置
                let top = event.clientY;
                let left = event.clientX;
                // 设置面板
                this.$refs.createClickPanel.style.top = `${top}px`;
                this.$refs.createClickPanel.style.left = `${left}px`;
                this.$refs.createClickPanel.style.display = "block";
                this.$refs.coverHook.style.display = "block";
                this.currentGroupId = groupId;
                this.currentGroupDom = event.currentTarget;
            },
            // 关闭右键面板
            closePanel() {
                this.$refs.joinClickPanel.style.display = "none";
                this.$refs.createClickPanel.style.display = "none";
                this.$refs.coverHook.style.display = "none";
            },
            // 群信息
            groupInfo() {
                this.closePanel();
                // 查询群信息
                groupApi.getGroupInfo(this.currentGroupId).then((response) => {
                    this.groupInfoObj = response.data.data;
                    // 查询群主信息
                    userApi.getUserInfo(response.data.data.creater).then((response1) => {
                        this.groupMaster = response1.data.data;
                        // 显示群信息Dialog
                        this.groupInfoDialog = true;
                    });
                });
            },
            // 群成员
            groupMember(type) {
                this.closePanel();
                this.currentGroupType = type;
                // 查询所有的群成员
                groupApi.getGroupMembers(this.currentGroupId).then((response) => {
                    this.groupMembers = response.data.data;
                    // 显示Dialog
                    this.groupMemberDialog = true;
                });
            },
            // 移除群成员
            removeMember(event, member_id) {
                let buttonDom = event.currentTarget;
                // 确认删除提示
                this.$confirm('此操作将永久删除该群成员, 是否继续?', '删除群成员', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'error'
                }).then(() => {
                    // 移除
                    groupApi.removeGroupMember(this.currentGroupId, member_id, "0").then((response) => {
                        this.$message({
                            type: (response.data.flag ? 'success' : "error"),
                            message: response.data.message,
                        });
                        // 移除该列成员
                        if (response.data.flag) {
                            $(buttonDom).parent().parent().remove();
                        }
                    });
                });
            },
            // 退出群聊
            quitGroup() {
                this.closePanel();
                // 确认退出提示
                this.$confirm('是否确定退出该群聊?', '退出群聊', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'error'
                }).then(() => {
                    // 退出
                    groupApi.removeGroupMember(this.currentGroupId, getUser().id, "1").then((response) => {
                        this.$message({
                            type: (response.data.flag ? 'success' : "error"),
                            message: response.data.message,
                        });
                        // 移除该群
                        if (response.data.flag) {
                            $(this.currentGroupDom).remove();
                        }
                    });
                });
            },
            // 解散群聊
            dissolutionGroup() {
                this.closePanel();
                // 确定解散
                this.$confirm('一旦解散不可恢复，是否确定解散该群聊?', '解散群聊', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'error'
                }).then(() => {
                    // 退出
                    groupApi.deleteGroup(this.currentGroupId).then((response) => {
                        this.$message({
                            type: (response.data.flag ? 'success' : "error"),
                            message: response.data.message,
                        });
                        // 移除该群聊
                        if (response.data.flag) {
                            $(this.currentGroupDom).remove();
                        }
                    });
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

    .group-list
        border-top 1px solid #dedede

        li
            display flex
            padding 3px 10px
            cursor pointer
            background-color #fffcf5

            &.hover:hover
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
                line-height 46px !important
                text-overflow ellipsis
                overflow hidden
                white-space nowrap
                padding-left 10px
                color #666
                border-bottom 1px solid #dedede

        .i-created
            height 45px
            line-height 45px
            background-color #e8e5de

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

    .group-picture
        width 56px
        height 56px

        img
            margin 3px
            height 50px
            width 50px
            border-radius 12px

    .group-info-item
        height 60px
        line-height 60px

        .left
            float left
            margin-right 10px
            font-weight 700
            font-size 16px

        .right
            float left

            .img
                float left
                width 46px
                height 46px

                img
                    margin 3px
                    width 40px
                    height 40px
                    border-radius 12px

            .name
                margin-left 10px
                float left

    .member
        margin-top 5px
        display flex

        &:hover
            background-color #f9f9f9

        .img
            flex 0 0 46px
            width 46px
            height 46px

            img
                margin 3px
                width 40px
                height 40px
                border-radius 12px

        .name
            flex 1
            padding-left 10px
            height 46px
            line-height 46px
            border-bottom 1px solid #dedede
            text-overflow ellipsis
            overflow hidden
            white-space nowrap

        .del-button
            flex 1

            button
                margin-top 12px
</style>
