<template>
    <li>
        <i class="iconfont icon-xiaoxi" @click="getInfo($event)"></i>
        <el-badge :value="$store.state.user.warnCount" class="item" type="danger" v-show="$store.state.user.warnCount"></el-badge>
        <!-- 消息面板 -->
        <el-dialog title="验证消息" :visible.sync="centerDialogVisible" width="30%" center>
            <div class="message">
                <div class="msg-item" v-for="(item, index) in confirmList" :key="index">
                    <div class="delete" @click="deleteConfirm(item.otherId, item.type)">
                        <i class="el-icon-circle-close-outline"></i>
                    </div>
                    <div class="img">
                        <img :src="item.avatar" alt="NO IMG">
                    </div>
                    <div class="text">
                        <!-- 请求添加 -->
                        <div class="nick" v-if="item.type === '0'">
                            {{ item.name }} 申请加为好友
                        </div>
                        <div class="nick" v-if="item.type === '1'">
                            {{ item.name }} 申请加入
                            <span style="color: #f90;">{{ item.groupName }}</span>
                        </div>
                        <!-- 添加通过 -->
                        <div class="nick" v-if="item.type === '2'">
                            {{ item.name }} <span style="color: #8ae27a;">成功操作</span>
                        </div>
                        <!-- 删除好友跟群 -->
                        <div class="nick" v-if="item.type === '3'">
                            {{ item.name }} <span style="color: #f00;">删除操作</span>
                        </div>

                        <div class="confirm-info" @click="infoDetails">
                            验证消息：{{ item.confirmInfo }}
                        </div>
                    </div>
                    <!-- 请求添加好友 -->
                    <div class="button" v-if="item.type === '0'">
                        <el-button type="primary" plain size="mini" @click="confirmAddUser(item.otherId)">同意</el-button>
                    </div>
                    <!-- 申请添加群 -->
                    <div class="button" v-if="item.type === '1'">
                        <el-button type="success" plain size="mini" @click="confirmAddGroup(item.otherId, item.applyId)">同意</el-button>
                    </div>
                    <!-- 添加成功 -->
                    <div class="button" v-if="item.type === '2'">
                        <i class="el-icon-success" style="color: #b2e281; font-size: 28px; line-height: 40px;"></i>
                    </div>
                    <!-- 被删除提示 -->
                    <div class="button" v-if="item.type === '3'">
                        <i class="el-icon-warning" style="color: #f00; font-size: 28px; line-height: 40px;"></i>
                    </div>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button type="success" @click="centerDialogVisible = false" plain>关 闭 验 证</el-button>
            </span>
        </el-dialog>
        <!-- 消息提醒 -->
        <audio id="notification_msg_hook">
            <source src="radio/system.wav"/>
            <source src="radio/system.mp3"/>
            <source src="radio/system.ogg"/>
        </audio>
    </li>
</template>

<script>
    import {getUser} from "../../utils/auth";
    import "../../api/user";
    import userApi from "../../api/user";
    import groupApi from "../../api/group";

    export default {
        data() {
            return {
                centerDialogVisible: false,
                confirmList: [],
                wsUser: null,
                wsGroup: null,
            };
        },
        created() {
            // 加载验证消息
            userApi.loadVerify(getUser().id).then((response) => {
                if (response.data.flag) {
                    this.confirmList = response.data.data;
                    this.$store.dispatch("user/setWarnCount", this.confirmList.length);
                }
            });
        },
        mounted() {
            /**
             * websocket发送验证消息，好友加好友
             */
            this.wsConfirm = new WebSocket("ws://127.0.0.1:9002/verify_user/" + getUser().id);
            this.wsConfirm.onmessage = (event) => {
                // 声音提示
                document.getElementById("notification_msg_hook").play();

                let data = JSON.parse(event.data);
                let flag = true;
                this.confirmList.forEach((item) => {
                    if (item.otherId === data[0] && item.type === "0") {
                        flag = false;
                    }
                });
                // 在验证消息栏中添加一条记录
                if (flag) {
                    let obj = {
                        otherId: data[0],
                        name: data[2],
                        avatar: data[3],
                        confirmInfo: data[1],
                        type: data[4],
                    };
                    this.$store.dispatch("user/incremWarnCount");
                    this.confirmList.push(obj);
                }
                if (data[4] === "2" || data[4] === "3") {
                    // 刷新群列表
                    // 重新加载好友列表
                    this.$store.dispatch("user/setRefreshFriendList", new Date());
                }
            };

            /**
             * websocket发送验证消息，群与好友之间
             */
            this.wsGroup = new WebSocket("ws://127.0.0.1:9003/verify_group/" + getUser().id);
            this.wsGroup.onmessage = (event) => {
                let data = JSON.parse(event.data);
                let flag = true;
                this.confirmList.forEach((item) => {
                    if (item.otherId === data[0] && item.type === "1") {
                        flag = false;
                    }
                });
                // 在验证消息栏中添加一条记录
                if (flag) {
                    let obj = {
                        otherId: data[0],
                        name: data[2],
                        avatar: data[3],
                        confirmInfo: data[1],
                        type: data[4],
                        groupName: data[5],
                        applyId: data[6],
                    };
                    this.$store.dispatch("user/incremWarnCount");
                    this.confirmList.push(obj);
                }
                if (data[4] === "2" || data[4] === "3") {
                    // 刷新群列表
                    // 重新加载群列表
                    this.$store.dispatch("group/setRefreshGroupList", new Date());
                }
            };
        },
        methods: {
            getInfo() {
                this.centerDialogVisible = true;
            },
            infoDetails(event) {
                // 获取内容
                let msg = event.currentTarget.innerHTML;
                this.$alert(msg, '验证消息', {
                    confirmButtonText: '关闭',
                    type: 'success',
                });
            },
            // 删除信息框
            deleteConfirm(id, type) {
                // 从redis中删除
                userApi.removeConfirmInfo(getUser().id, id, type).then((response) => {
                    if (response.data.flag) {
                        // 删除该条请求
                        this.removeFromConfirmList(id, type);
                    }
                });
            },
            // 同意添加好友
            confirmAddUser(id) {
                userApi.allowFriendApply(getUser().id, id).then((response) => {
                    if (response.data.flag) {
                        // 添加成功，删除该条请求
                        this.removeFromConfirmList(id, "0");
                        // 重新加载好友列表
                        this.$store.dispatch("user/setRefreshFriendList", new Date());
                    }
                });
            },
            // 同意用户加入群
            confirmAddGroup(groupId, applyId) {
                groupApi.confirmAddGroup(groupId, applyId, getUser().id).then((response) => {
                    if (response.data.flag) {
                        // 添加成功，删除该请求
                        this.removeFromConfirmList(groupId, "1");
                    }
                });
            },
            // 从列表中移除好友申请
            removeFromConfirmList(id, type) {
                // 从列表中移除该好友申请
                this.confirmList.forEach((item) => {
                    if (id === item.otherId && type === item.type) {
                        let index = this.confirmList.indexOf(item);
                        this.confirmList.splice(index, 1);
                    }
                });
                // 验证消息减一
                this.$store.dispatch("user/decremWarnCount");
                if (this.$store.state.user.warnCount === 0) {
                    document.getElementsByTagName("sup")[0].style.display = "none";
                }
            },
        },
    }
</script>

<style scoped lang="stylus">
    li
        position relative

        .item
            position absolute
            top -8px
            right -6px

        i
            color #5a5a5a

        &:hover
            i
                color #000

        .msg-item
            position relative
            display flex
            margin-bottom 20px
            border 1px solid #dedede
            padding 5px
            border-radius 4px
            box-shadow 2px 2px 6px #dedede

            .delete
                position absolute
                top -15px
                right -9px

                i
                    font-size 20px
                    color #535353
                    font-weight bolder

                    &:hover
                        color #f00

            .img
                flex 0 0 50px
                width 50px
                height 50px

                img
                    width 100%
                    height 100%
                    border-radius 12px

            .text
                flex 1
                padding-left 10px
                width 0

                .nick
                    width 100%
                    height 25px
                    line-height 25px
                    font-weight bolder

                .confirm-info
                    margin 0 10px 0 0
                    border-radius 4px
                    height 25px
                    line-height 25px
                    text-overflow ellipsis
                    overflow hidden
                    white-space nowrap

                    &:hover
                        background-color #f2f2f2

            .button
                flex 0 0 50px
                margin-top 8px
</style>
