<template>
    <div class="wrap">
        <div class="nav">
            <div class="left col-md-2 col-lg-2 col-sm-2">
                <div class="writing" title="写动态">
                    <i class="iconfont icon-biji"></i>
                </div>
                <ul class="left-list">
                    <li><i class="iconfont icon-xiaoxi"></i></li>
                    <li><i class="iconfont icon-solid-person"></i></li>
                    <li><i class="iconfont icon-youjian1"></i></li>
                    <li style="clear: both; display: none;"></li>
                </ul>
            </div>
            <div class="middle col-md-7 col-lg-7 col-sm-7">
                {{ userName }}的个人主页
            </div>
            <div class="right col-md-3 col-lg-3 col-sm-3">
                <ul class="right-list">
                    <li class="con">
                        <i class="iconfont icon-jiahaoyou"></i> 添加
                        <div class="add">
                            <ul class="add-list clear-float">
                                <li @click="addFriendDialog = true">
                                    <i class="glyphicon glyphicon-plus"></i>
                                    加好友
                                </li>
                                <li @click="addGroupDialog = true">
                                    <i class="glyphicon glyphicon-home"></i>
                                    加群
                                </li>
                            </ul>
                        </div>
                    </li>
                    <li class="con"><i class="iconfont icon-qunzu2"></i> 建群</li>
                    <li class="con"><i class="iconfont icon-shezhi-tianchong"></i> 设置</li>
                    <li class="img"><img :src="userAvatar" alt="NO IMG" :title="userName"></li>
                    <li style="clear: both; display: none;"></li>
                </ul>
            </div>
            <div style="clear: both;"></div>
        </div>
        <div class="body clear-float">
            <div class="left col-md-2 col-lg-2 col-sm-2" ref="leftHook">
                <div class="user-info">
                    <div class="img">
                        <div class="box">
                            <img :src="userAvatar" alt="NO IMG">
                        </div>
                    </div>
                    <div class="nick">{{ userName }}</div>
                    <div class="sign">沉思往事立残阳，当时只道是寻常，看得见开始，猜不到结局</div>
                </div>
                <div class="friend">
                    <div class="topic">
                        <i class="glyphicon glyphicon-user"></i>
                        好友列表
                    </div>
                    <div class="check-router">
                        <div class="friend-router friend-active" @click="friendList">
                            <i class="iconfont icon-solid-person"></i>
                            好友
                        </div>
                        <div class="group-router" @click="groupList">
                            <i class="iconfont icon-qunzu2"></i>
                            群组
                        </div>
                    </div>
                    <FriendList :friendList="friendListData" v-show="isFriend"/>
                    <GroupList :groupList="groupListData" v-show="isFriend === false"/>
                </div>
                <div class="dynamic">
                    <div class="topic">
                        <i class="glyphicon glyphicon-globe"></i>
                        最近动态
                    </div>
                    <div class="dynamic-item">
                        <a href="#" style="text-decoration: none !important;">
                            <div class="img"><img src="~/assets/images/bg1.jpg" alt="NO IMG"></div>
                            跟会挂号费的该地块发几个过大使馆的附近空该记录肯定是个句点符低功耗的书法家口广东省各环节的
                        </a>
                    </div>
                    <div class="dynamic-item">
                        <a href="#" style="text-decoration: none !important;">
                            <div class="img"><img src="~/assets/images/bg1.jpg" alt="NO IMG"></div>
                            跟会挂号费的该地块发几个过大使馆的附近空该记录肯定是个句点符低功耗的书法家口广东省各环节的
                        </a>
                    </div>
                    <div class="dynamic-item" style="text-decoration: none !important;">
                        <a href="#">
                            <div class="img"><img src="~/assets/images/bg1.jpg" alt="NO IMG"></div>
                            跟会挂号费的该地块发几个过大使馆的附近空该记录肯定是个句点符低功耗的书法家口广东省各环节的
                        </a>
                    </div>
                    <div class="dynamic-item" style="text-decoration: none !important;">
                        <a href="#">
                            <div class="img"><img src="~/assets/images/bg1.jpg" alt="NO IMG"></div>
                            跟会挂号费的该地块发几个过大使馆的附近空该记录肯定是个句点符低功耗的书法家口广东省各环节的
                        </a>
                    </div>
                </div>
            </div>
            <div class="middle col-md-8 col-lg-8 col-sm-8">
                <div class="banner">
                    这里可以做一个banner图，打广告
                </div>
                <div class="chat-pannel">
                    <div class="panel panel-success">
                        <!-- Default panel contents -->
                        <div class="panel-heading clear-float">
                            <CurrentChat :nick="$store.state.friend.currentNick"
                                         :avatar="$store.state.friend.currentAvatar" v-show="isChating"/>
                        </div>
                        <div class="panel-body" ref="chatPanelHook">
                            <!-- 私发 -->
                            <div v-for="(item, index) in $store.state.friend.chatRecord" :key="index"
                                 :class="'msg-' + item.status + ' msg clear-float'"
                                 v-if="$store.state.chat.sendType === 0">
                                <div class="user-wrap clear-float">
                                    <div class="header">
                                        <img class="user-img"
                                             :src="item.status === 'left' ? $store.state.friend.currentAvatar : userAvatar"
                                             alt="NO IMG">
                                    </div>
                                    <div class="body">
                                        <div class="trangle user-trangle"></div>
                                        <div class="text user-text">
                                            {{ item.message }}
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!-- 群聊 -->
                            <div v-for="(item, index) in $store.state.friend.chatRecord" :key="index"
                                 :class="'msg-' + item.status + ' msg clear-float'"
                                 v-if="$store.state.chat.sendType === 1">
                                <div class="name">{{ item.name }}</div>
                                <div class="user-wrap clear-float">
                                    <div class="header">
                                        <img class="user-img"
                                             :src="item.avatar"
                                             alt="NO IMG">
                                    </div>
                                    <div class="body">
                                        <div class="trangle user-trangle"></div>
                                        <div class="text user-text">
                                            {{ item.message }}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="panel-footer">
                            <textarea name="content" id="editor_id"></textarea>
                            <div class="is-enter">
                                <label>按ENTER键发送消息</label>
                                <el-switch
                                    v-model="isEnter"
                                    active-color="#13ce66"
                                    inactive-color="#ff4949">
                                </el-switch>
                            </div>
                            <div class="btn-area">
                                <button @click="closeChat" type="button" class="btn btn-default">关闭</button>
                                <button @click="sendMsg($store.state.friend.currentId, $store.state.chat.sendType)"
                                        :data-sendtype="$store.state.chat.sendType"
                                        :data-otherid="$store.state.friend.currentId"
                                        ref="sendBtnHook"
                                        type="button" class="btn btn-success">发送
                                </button>
                            </div>
                            <div style="clear: both;"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="right col-md-2 col-lg-2 col-sm-2" ref="rightHook">
                <div class="decorate"></div>
                <div class="related-chat">
                    <div class="topic">
                        <i class="glyphicon glyphicon-leaf"></i>
                        最近联系
                    </div>
                    <RelationChat :relationChatListData="relationChatListData"/>
                </div>
            </div>
        </div>
        <div class="bg"></div>
        <!-- 加好友弹框 -->
        <el-dialog title="添加好友" width="30%" :visible.sync="addFriendDialog">
            <el-form label-position="right" label-width="80px" :inline="true">
                <el-form-item label="搜索用户">
                    <el-input v-model="addFriendName" autocomplete="off" style="width: 376px;"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" circle @click="searchFriend"></el-button>
                </el-form-item>
            </el-form>

            <div class="user-list" v-show="searchUser.length">
                <div class="title">
                    搜索结果
                </div>
                <div class="user-item" v-for="(item, index) in searchUser" :key="index">
                    <div class="user-img"><img :src="item.avatar" alt="NO IMG"></div>
                    <div class="info">
                        <div class="nick">{{ item.name }}</div>
                        {{ item.sign }}
                    </div>
                    <div class="add-btn">
                        <el-button type="success" @click="addFriend(item.id)">添加好友</el-button>
                    </div>
                </div>
            </div>

            <div class="add-friend" v-show="addFriendFlag">
                <el-form ref="form" label-width="120px">
                    <el-form-item label="请填写验证信息">
                        <el-input v-model="addFriendMap.confirmInfo"></el-input>
                    </el-form-item>
                    <el-form-item label="备注">
                        <el-input v-model="addFriendMap.todo"></el-input>
                    </el-form-item>
                    <el-form-item label="分组">
                        <el-select v-model="addFriendMap.group">
                            <el-option v-for="(item, index) in groupArray" :key="index"
                                       :label="item.name" :value="item.id"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item size="large">
                        <el-button type="primary" @click="confirmAddFriend(addFriendId)">确认添加</el-button>
                        <el-button @click="cancelAddFriend">取消</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </el-dialog>

        <!-- 加群 -->
        <el-dialog title="添加群聊" width="30%" :visible.sync="addGroupDialog">
            <el-form label-position="right" label-width="90px" :inline="true">
                <el-form-item label="搜索群名称">
                    <el-input v-model="addGroupName" autocomplete="off" style="width: 376px;"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button icon="el-icon-search" circle @click="searchGroup"></el-button>
                </el-form-item>
            </el-form>

            <!-- 展示群聊搜索结果 -->
            <div class="user-list" v-show="searchGroupList.length">
                <div class="title">
                    搜索结果
                </div>
                <div class="user-item" v-for="(item, index) in searchGroupList" :key="index">
                    <div class="user-img"><img :src="item.avatar" alt="NO IMG"></div>
                    <div class="info">
                        <div class="nick">{{ item.name }}</div>
                    </div>
                    <div class="add-btn">
                        <el-button type="success" @click="addGroup(item.id)">申请加群</el-button>
                    </div>
                </div>
            </div>

            <!-- 显示添加群聊信息 -->
            <div class="add-friend" v-show="addGroupFlag">
                <el-form ref="form" label-width="120px">
                    <el-form-item label="请填写验证信息">
                        <el-input v-model="confirmInfo"></el-input>
                    </el-form-item>
                    <el-form-item size="large">
                        <el-button type="primary" @click="confirmAddGroup">确认申请</el-button>
                        <el-button @click="cancelAddGroup">取消</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import "~/assets/css/bootstrap.min.css";
    import "~/assets/css/chat.css";
    import FriendList from "@/components/friendList/friendList";
    import GroupList from "@/components/groupList/groupList";
    import CurrentChat from "@/components/currentChat/currentChat";
    import RelationChat from "@/components/relationChat/relationChat";
    import {getUser} from "../../utils/auth";
    import chatApi from "../../api/chat";
    import userApi from "../../api/user";
    import friendApi from "../../api/friend";
    import groupApi from "../../api/group";
    import axios from "axios";

    export default {
        data() {
            return {
                isEnter: false,
                isFriend: true,
                userAvatar: getUser().avatar,
                userName: getUser().nick,
                isChating: false,
                editor: null,
                // 添加朋友
                addFriendDialog: false,
                addFriendName: "",
                searchUser: [],
                addFriendMap: {
                    confirmInfo: "",
                    todo: "",
                    group: 0,
                },
                addFriendFlag: false,
                groupArray: [],
                addFriendId: 0,
                // 添加群聊
                addGroupDialog: false,
                addGroupName: "",
                searchGroupList: [],
                addGroupFlag: false,
                confirmInfo: "大家好，我是" + getUser().nick,
                addGroupId: 0,
            };
        },
        mounted() {
            // 设置下面的div的高度
            let height = window.innerHeight - 60;
            this.$refs.leftHook.style.minHeight = height + "px";
            this.$refs.rightHook.style.minHeight = height + "px";

            // 加载富文本框
            KindEditor.ready((K) => {
                this.editor = K.create(
                    '#editor_id',
                    {
                        themeType: 'zw',
                        resizeType: 0, // 是否可以拖拽文本框的大小 0：不能  1： 只能上下  2：上下左右均可
                        allowPreviewEmoticons: true,    // 为true时，鼠标放到表情上可以预览
                        items: [
                            "emoticons", 'image', "insertfile",
                        ],           // 配置工具栏的参数，  "/"表示换行   "|"表示分割符
                        width: '100%',       // 文本框宽度(可以百分比或像素)
                        height: '150px',     // 文本框高度(只能像素)
                        htmlTags: {"img": []},
                    }
                );
            });

        },
        asyncData() {
            let userId = getUser().id;
            // 初始化数据
            return axios.all([
                chatApi.getUserList("friend_list", userId),
                chatApi.getUserList("group_list", userId),
                chatApi.getUserList("relation_chat", userId),
            ]).then(axios.spread(function (friendList, groupList, relationChatList) {
                return {
                    friendListData: friendList.data.data,
                    groupListData: groupList.data.data,
                    relationChatListData: relationChatList.data.data,
                };
            }));
        },
        methods: {
            friendList() {
                $(".group-router").removeClass("friend-active");
                $(".friend-router").addClass("friend-active");
                this.isFriend = true;
            },
            groupList() {
                $(".group-router").addClass("friend-active");
                $(".friend-router").removeClass("friend-active");
                this.isFriend = false;
            },
            // 关闭当前聊天
            closeChat() {
                this.isChating = false;
                this.$store.dispatch("friend/setChatRecord", []);
                this.$store.dispatch("friend/setCurrentId", 0);
            },
            // 发送消息
            sendMsg(sendId, sendType) {
                if (sendId === 0) {
                    // 没有选择发送的对象
                    this.$message({
                        message: '请选择发送对象',
                        type: 'error'
                    });
                    return;
                }
                // 获取要发送的消息
                let msg = this.editor.html().trim();
                if (msg === "") {
                    this.$message({
                        message: '不能发送空消息',
                        type: 'info'
                    });
                    return;
                }
                // 准备就绪，发送消息
                let msgStr = '';
                if (sendType === 0) {
                    // 个人对个人发送消息
                    // 将该消息发送到自己的屏幕上
                    msgStr = `
                        <div class="msg-right msg clear-float">
                            <div class="user-wrap clear-float">
                                <div class="header">
                                    <img class="user-img" src="${this.userAvatar}" alt="NO IMG">
                                </div>
                                <div class="body">
                                    <div class="send-status">
                                        <i class="el-icon-loading"></i>
                                    </div>
                                    <div class="trangle user-trangle"></div>
                                    <div class="text user-text">
                                        ${msg}
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                } else if (sendType === 1) {
                    // 个人对群发送消息
                    // 将该消息发送到自己的屏幕上
                    msgStr = `
                        <div class="msg-right msg clear-float">
                            <div class="name">${this.userName}</div>
                            <div class="user-wrap clear-float">
                                <div class="header">
                                    <img class="user-img" src="${this.userAvatar}" alt="NO IMG">
                                </div>
                                <div class="body">
                                    <div class="send-status">
                                        <i class="el-icon-loading"></i>
                                    </div>
                                    <div class="trangle user-trangle"></div>
                                    <div class="text user-text">
                                        ${msg}
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                }
                let msgDom = $(msgStr);
                $(this.$refs.chatPanelHook).append(msgDom);    // 添加到聊天记录
                // 刷新div到底部显示
                this.$refs.chatPanelHook.scrollTop = this.$refs.chatPanelHook.scrollHeight;
                this.editor.html('');
                let sendStr = sendType === 0 ? "send_msg_personnal" : "send_msg_group";
                chatApi.sendMessage(sendStr, getUser().id, sendId, msg).then((response) => {
                    let statusDom = msgDom.children(".user-wrap").children(".body").children(".send-status");
                    if (response.data.flag) {
                        // 发送成功，移除等待标志
                        statusDom.remove();
                    } else {
                        this.$message({
                            message: '发送失败，请重试',
                            type: 'error'
                        });
                        // 变换等待标志
                        statusDom.children("i").removeClass("el-icon-loading").addClass("el-icon-warning").css("color", "#f00");
                    }
                });
            },
            // 回车事件
            keyUpEvent(event) {
                if (event.keyCode === 13 && !event.ctrlKey) {
                    let sendType = this.$refs.sendBtnHook.dataset.sendtype * 1;
                    let sendId = this.$refs.sendBtnHook.dataset.otherid * 1;
                    // 发送消息
                    this.sendMsg(sendId, sendType);
                    return false;
                }
            },
            /**
             * 添加好友
             */
            // 搜索用户
            searchFriend() {
                if (this.addFriendName.trim() === "") {
                    this.$message({
                        message: '用户名不能为空',
                        type: 'info'
                    });
                    return;
                }
                // 进行搜索
                userApi.searchUser(this.addFriendName).then((response) => {
                    if (response.data.flag || response.data.data.length === 0) {
                        this.searchUser = response.data.data;
                        this.addFriendFlag = false;
                    } else {
                        this.$message({
                            message: '没有该用户',
                            type: 'error'
                        });
                    }
                });
            },
            // 添加好友
            addFriend(friendId) {
                this.searchUser = [];     // 隐藏用户搜索内容
                this.addFriendFlag = true;   // 显示添加用户
                // 设置默认备注跟默认验证信息
                this.addFriendMap.confirmInfo = "你好，我是" + getUser().nick;
                this.addFriendMap.todo = this.addFriendName;
                // 获取用户的分组
                userApi.getGroupList(getUser().id).then((response) => {
                    this.groupArray = response.data.data;
                    this.addFriendMap.group = response.data.data[0].id;
                });
                this.addFriendId = friendId;
            },
            // 确认添加好友
            confirmAddFriend(friendId) {
                if (this.addFriendMap.todo.trim() === "") {
                    this.$message({
                        message: '请设置用户备注',
                        type: 'info'
                    });
                    return;
                }
                // 进行添加
                friendApi.addFriend(getUser().id, friendId).then((response) => {
                    this.$notify({
                        title: (response.data.flag ? "成功" : "失败"),
                        message: (response.data.flag ? response.data.message + "，请等待对方同意。" : response.data.message),
                        type: (response.data.flag ? "success" : "error"),
                    });
                    if (response.data.flag) {
                        this.cancelAddFriend();
                    }
                });
            },
            // 取消添加好友
            cancelAddFriend() {
                this.addFriendDialog = false;
                this.addFriendName = "";
                this.searchUser = [];
                this.addFriendFlag = false;
                this.addFriendId = 0;
            },
            /**
             * 添加群聊
             */
            // 搜索群
            searchGroup() {
                if (this.addGroupName.trim() === "") {
                    this.$message({
                        message: '群名称不能为空',
                        type: 'info'
                    });
                    return;
                }
                // 搜索群聊
                groupApi.searchGroupByName(this.addGroupName).then((response) => {
                    if (response.data.flag || response.data.data.length === 0) {
                        this.searchGroupList = response.data.data;
                        this.addGroupFlag = false;
                    } else {
                        this.$message({
                            message: '没有该群聊',
                            type: 'error'
                        });
                    }
                });
            },
            // 添加群
            addGroup(groupId) {
                this.searchGroupList = [];     // 隐藏群聊搜索内容
                this.addGroupFlag = true;   // 显示添加群组
                this.addGroupId = groupId;
            },
            // 填写完信息，确认添加群
            confirmAddGroup() {
                if (this.confirmInfo.trim() === "") {
                    this.$message({
                        message: '请填写验证信息',
                        type: 'info'
                    });
                    return;
                }
                // 进行添加
                groupApi.addGroup(getUser().id, this.addGroupId, this.confirmInfo).then((response) => {
                    this.$notify({
                        title: (response.data.flag ? "成功" : "失败"),
                        message: (response.data.flag ? "添加成功，请等待审核通过。" : response.data.message),
                        type: (response.data.flag ? "success" : "error"),
                    });
                    if (response.data.flag) {
                        this.cancelAddGroup();
                    }
                });
            },
            // 取消添加群
            cancelAddGroup() {
                this.addGroupDialog = false;
                this.addGroupName =  "";
                this.searchGroupList = [];
                this.addGroupFlag = false;
                this.confirmInfo = "大家好，我是" + getUser().nick;
                this.addGroupId = 0;
            },
        },
        computed: {
            "isChatingComputed": function () {
                return this.$store.state.friend.currentNick;
            },
        },
        watch: {
            isChatingComputed: function () {
                // 清空当前的聊天记录
                this.isChating = true;
                // 清空输入框
                this.editor.html("");
                // 将滚动条滚动到最下方
                this.$refs.chatPanelHook.scrollTop = this.$refs.chatPanelHook.scrollHeight;
            },
            // 是否开启回车键发送消息
            isEnter: function () {
                let $txt = $("iframe").contents().find("body");
                if (this.isEnter) {
                    // 开启
                    $txt.bind("keydown", this.keyUpEvent);
                } else {
                    // 关闭
                    $txt.unbind("keydown", this.keyUpEvent);
                }
            },
        },
        components: {
            FriendList,
            GroupList,
            CurrentChat,
            RelationChat,
        },
    }
</script>

<style scoped lang="stylus">
    .clear-float
        content ""
        display block
        clear both

    .wrap
        width 80%
        margin 0 auto

        .bg
            position fixed
            top 0
            bottom 0
            left 0
            right 0
            z-index -1
            background-color #f2efe8

        .nav
            width 100%
            height 60px
            background-color #fffcf5
            border 1px solid #dedede
            overflow hidden

            .left
                .writing
                    margin 10px
                    width 40px
                    height 40px
                    background-color #85d5fc
                    line-height 40px
                    text-align center
                    color #fff
                    border-radius 50%
                    font-weight 700
                    font-size 18px
                    cursor pointer
                    float left

                    &:hover
                        background-color #5dc6e5

                .left-list
                    float left
                    margin 13px 0 0 13px

                    li
                        float left
                        margin-right 15px
                        width 35px
                        height 35px
                        line-height 35px
                        text-align center
                        border 1px solid #dedede
                        border-radius 50%
                        cursor pointer

                        &:hover
                            i
                                color #000

                        i
                            color #5a5a5a


            .middle
                height 100%
                line-height 60px
                text-align center
                color #85d5fc
                font-weight 700
                font-size 22px
                text-shadow 2px 2px 6px #767676

            .right
                .right-list
                    height 100%
                    overflow hidden

                    .con
                        margin 6px 10px
                        padding 5px 10px
                        float left
                        background-color rgba(91, 188, 218, 0.19)
                        border-radius 18px
                        box-shadow 2px 2px 6px #d6d6d6
                        border 1px solid rgba(91, 188, 218, 0.29)
                        cursor pointer
                        color #575757

                        &:nth-child(1)
                            position relative

                            &:hover
                                .add
                                    width 122px

                            .add
                                width 0
                                overflow hidden
                                transition width .5s ease
                                position absolute
                                top -3px
                                left 70px

                                .add-list
                                    width 120px
                                    background-color #fff
                                    border-radius 8px
                                    overflow hidden
                                    border 1px solid #dedede

                                    li
                                        padding 0 10px !important
                                        border-bottom 1px solid #dedede
                                        vertical-align middle

                                        i
                                            margin-right 3px
                                            font-weight 100
                                            font-size 14px
                                            vertical-align bottom
                                            line-height 24px
                                            color #85d5fc

                                        &:hover
                                            background-color #fffcf5


                        &:hover
                            background-color rgba(91, 188, 218, 0.29)

                        i
                            font-size 22px
                            vertical-align middle

                    .img
                        float right
                        margin 5px 0
                        width 50px
                        height 50px
                        border-radius 50%
                        overflow: hidden

                        img
                            height 100%
                            width 100%

        .body
            .left
                padding 0 0 60px 0 !important
                height 100%
                background-color #85d5fc

                .user-info
                    margin-top 50px

                    .img
                        width 100%
                        height 70px

                        .box
                            margin 0 auto
                            width 70px
                            height 70px

                            img
                                width 100%
                                height 100%
                                border-radius 50%

                    .nick
                        color #fff
                        font-weight 700
                        font-size 18px
                        text-align center
                        line-height 40px

                    .sign
                        padding 5px 10px
                        color #fff
                        line-height 30px
                        text-align center


                .friend
                    margin-top 30px

                    .topic
                        color #fff
                        background-color #f32e59
                        text-align center
                        height 40px
                        line-height 40px

                    .check-router
                        margin-top 2px
                        display flex
                        height 50px
                        color #fff

                        .friend-active
                            background-color #b2e281

                        div
                            flex 1
                            line-height 50px
                            text-align center
                            border-radius 12px 12px 0 0
                            background-color #5dc6e4
                            cursor pointer
                            overflow: hidden
                            margin 0 2px

                            &:hover
                                background-color #b2e281

                .dynamic
                    margin-top 60px

                    .topic
                        height 40px
                        line-height 40px
                        background-color #b2e281
                        color #fff
                        text-align center

                    .dynamic-item
                        padding 5px 10px
                        border-bottom 1px solid #dedede

                        a
                            text-decoration none
                            color #565656

                        .img
                            float left
                            width 80px
                            height 60px
                            border 5px solid #dedede
                            margin-right 5px

                            img
                                width 100%
                                height 100%

                        .text
                            overflow hidden

            .middle
                .banner
                    margin 20px 10px
                    height 160px
                    color #fff
                    font-weight 700
                    font-size 28px
                    text-align center
                    line-height 160px
                    background-color #5bc0de

                .chat-pannel
                    .panel-heading
                        height 61px

                    .panel-body
                        padding 20px;
                        height 400px
                        overflow auto

                    .panel-footer
                        .is-enter
                            float left
                            margin-top 20px

                        .btn-area
                            float right
                            margin-top 10px

            .right
                padding 0 0 60px 0 !important
                height 100%
                border 1px solid #dedede
                background-color #fffcf5

                .decorate
                    height 23px
                    background-image url("../../assets/images/personnal_page/could.png")
                    background-size 55px 23px

                .related-chat
                    .topic
                        margin-top 5px
                        height 40px
                        line-height 40px
                        background-color #85d5fc
                        color #fff
                        text-align center

        .user-list
            margin 0 auto
            width 70%

            .title
                margin-bottom 5px
                font-weight 700
                font-size 20px

            .user-item
                display flex
                margin-bottom 10px
                padding 5px
                cursor pointer
                border 1px solid #dedede
                border-radius 4px
                box-shadow 2px 2px 6px #c8c8c8

                &:hover
                    background-color #f6f6f6

            .user-img
                flex 0 0 46px
                width 46px
                height 46px

                img
                    margin 3px
                    width 40px
                    height 40px
                    border-radius 12px

            .info
                flex 1
                padding 0 10px
                width 0
                white-space nowrap
                text-overflow ellipsis
                overflow hidden

            .add-btn
                flex 0 0 60px
                margin-top 3px

        .add-friend
            padding 10px 20px
            border-radius 8px
            box-shadow 2px 2px 6px #c8c8c8
            border 1px solid #dedede
</style>
