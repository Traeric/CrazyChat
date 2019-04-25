<template>
    <div class="wrap">
        <div class="nav">
            <div class="left col-md-2 col-lg-2 col-sm-2">
                <div class="writing" title="写动态">
                    <i class="iconfont icon-biji"></i>
                </div>
                <ul class="left-list">
                    <Notification/>
                    <li><i class="iconfont icon-youjian1"></i></li>
                    <li title="个人信息" @click="personnalInfo"><i class="iconfont icon-solid-person"></i></li>
                    <li style="clear: both; display: none;"></li>
                </ul>
            </div>
            <div class="middle col-md-7 col-lg-7 col-sm-7">
                {{ userName }}的个人主页
            </div>
            <div class="right col-md-3 col-lg-3 col-sm-3">
                <ul class="right-list">
                    <li class="logout">
                        <el-button @click="logout" size="small" type="danger">登出</el-button>
                    </li>
                    <li class="con" @click="manageGroup">
                        <i class="glyphicon glyphicon-tags"></i> 管理分组
                    </li>
                    <li class="con">
                        <i class="iconfont icon-jiahaoyou"></i> 添加
                        <div class="add">
                            <ul class="add-list clear-float">
                                <li @click="addFriend">
                                    <i class="glyphicon glyphicon-plus"></i>
                                    加好友
                                </li>
                                <li @click="addGroup">
                                    <i class="glyphicon glyphicon-home"></i>
                                    加群
                                </li>
                            </ul>
                        </div>
                    </li>
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
                    <div class="sign">{{ userInfo.sign }}</div>
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
                <div class="link-group">
                    <el-row>
                        <el-button type="text">快捷连接：</el-button>
                        <el-button type="primary">
                            <nuxt-link to="/home_page/build_group"
                                       style="text-decoration: none; color: #fff; display: block;">
                                新建群聊<i class="el-icon-circle-plus el-icon--right"></i>
                            </nuxt-link>
                        </el-button>
                        <el-button type="danger">
                            <nuxt-link to="/settings"
                                       style="text-decoration: none; color: #fff; display: block;">
                                用户设置<i class="el-icon-setting el-icon--right"></i>
                            </nuxt-link>
                        </el-button>
                        <el-button type="success">
                            <nuxt-link to="/chat_room" style="color: #fff; text-decoration: none; display: block;">
                                聊天室<i class="el-icon-back el-icon--right" style="transform: rotate(180deg);"></i>
                            </nuxt-link>
                        </el-button>
                        <el-button type="warning">动态空间<i class="el-icon-star-on el-icon--right"></i></el-button>
                    </el-row>
                </div>
                <div class="chat-pannel">
                    <div class="panel panel-success">
                        <!-- Default panel contents -->
                        <div class="panel-heading clear-float">
                            <CurrentChat :nick="$store.state.friend.currentNick"
                                         :avatar="$store.state.friend.currentAvatar" v-show="isChating"/>
                        </div>
                        <div class="panel-body" ref="chatPanelHook">
                            <!-- 聊天面板 -->
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

        <!-- 背景 -->
        <div class="bg"></div>

        <!-- 加好友弹框 -->
        <AddFriend/>
        <!-- 加群 -->
        <AddGroup/>
        <!-- 个人信息 -->
        <PersonnalInfo/>
        <!-- 分组管理 -->
        <UserGroup/>
        <!-- 消息提醒 -->
        <audio id="msg_hook">
            <source src="radio/msg.wav"/>
            <source src="radio/msg.mp3"/>
            <source src="radio/msg.ogg"/>
        </audio>
    </div>
</template>

<script>
    import "~/assets/css/bootstrap.min.css";
    import "~/assets/css/chat.css";
    import FriendList from "../../components/friendList/friendList";
    import GroupList from "../../components/groupList/groupList";
    import CurrentChat from "../../components/currentChat/currentChat";
    import RelationChat from "../../components/relationChat/relationChat";
    import AddFriend from "../../components/addFriend/addFriend";
    import AddGroup from "../../components/addGroup/addGroup";
    import PersonnalInfo from "../../components/personnalInfo/personnalInfo";
    import UserGroup from "../../components/userGroup/userGroup";
    import Notification from "../../components/notification/notification";
    import {getUser, removeUser} from "../../utils/auth";
    import chatApi from "../../api/chat";
    import userApi from "../../api/user";
    import relationChatApi from "../../api/relation_chat";
    import axios from "axios";

    export default {
        data() {
            return {
                isEnter: false,
                isFriend: true,
                userAvatar: "",
                userName: "",
                userId: "",
                isChating: false,
                editor: null,
                userInfo: {},
                friendListData: {},
                groupListData: [],
                relationChatListData: [],
                wsMsg: null,
            };
        },
        created() {
            // 判断登录
            if (!getUser().id) {
                // 未登录
                this.$router.push("/login");
                return;
            }

            // 初始化用户头像
            this.userAvatar = getUser().avatar;
            this.userName = getUser().nick;
            this.userId = getUser().id;
            // 初始化签名
            userApi.getUserInfo(getUser().id).then((response) => {
                this.userInfo = response.data.data;
            });
            /**
             * 加载页面数据
             */
                // 获取用户id
            let userId = getUser().id;
            // 初始化数据
            axios.all([
                chatApi.getUserFriendList(userId),
                chatApi.getUserGroupList(userId),
                chatApi.getRelationChatList(userId),
            ]).then(axios.spread((friendList, groupList, relationChatList) => {
                this.friendListData = friendList.data.data;
                this.groupListData = groupList.data.data;
                // 排序
                let data = relationChatList.data.data;
                // 排序聊天记录
                data.sort((c1, c2) => {
                    return c2.sort - c1.sort;
                });
                this.relationChatListData = data;
            }));
        },
        mounted() {
            // 设置两侧的div的高度
            let height = window.innerHeight - 60;
            this.$refs.leftHook.style.minHeight = height + "px";
            this.$refs.rightHook.style.minHeight = height + "px";
            // 加载富文本框
            this.editor = KindEditor.create(
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
                    htmlTags: {"img": ["src", "width"]},
                }
            );
            // 替换图片按钮以及文件按钮
            let imageDom = $("span[data-name=image]");
            imageDom.css("position", "relative");
            imageDom.append(`<input type="file" id="image_input"
                style="position: absolute; width: 20px;height: 18px; top: 0; left: 0; opacity: 0; cursor: pointer;">`);
            // 添加点击事件
            imageDom.on("input", "input[type=file]", this.uploadImage);
            let fileDom = $("span[data-name=insertfile]");
            fileDom.css("position", "relative");
            fileDom.append(`<input type="file" id="file_input"
                style="position: absolute; width: 20px;height: 18px; top: 0; left: 0; opacity: 0; cursor: pointer;">`);
            fileDom.on("input", "input[type=file]", this.uploadFile);

            /**
             * websocket发送消息操作
             */
            // 建立socket连接
            this.wsMsg = new WebSocket("ws://127.0.0.1:9001/chat_user_to_user/" + getUser().id);
            this.wsMsg.onmessage = (event) => {
                // 消息提示音
                document.getElementById("msg_hook").play();

                let data = JSON.parse(event.data);

                // 将聊天对象添加到最近联系人
                relationChatApi.deleteRelationChat(getUser().id, data[0]).then((response) => {
                    if (response.data.flag) {
                        relationChatApi.addRelationChat(getUser().id, data[0], data[1]).then((response) => {
                            if (response.data.flag) {
                                chatApi.getRelationChatList(getUser().id).then((response) => {
                                    let data = response.data.data;
                                    // 排序聊天记录
                                    data.sort((c1, c2) => {
                                        return c2.sort - c1.sort;
                                    });
                                    this.relationChatListData = data;
                                });
                            }
                        });
                    }
                });

                let msg = "";
                // 当前聊天面板的id
                let currentChatId = this.$store.state.friend.currentId;
                // 首先判断用户面板聊天的人是否是当前发消息的对象
                if (currentChatId !== (data[0] + '')) {
                    // 不是当前用户
                    // 向redis中存入一条未读记录
                    chatApi.addUnRead(getUser().id, data[0] + '').then((response) => {
                        if (response.data.flag) {
                            // 添加成功，更新最近聊天列表
                            chatApi.getRelationChatList(getUser().id).then((response) => {
                                this.relationChatListData = response.data.data;
                            });
                        }
                    });
                    return;
                }
                if (data[1] === "0") {
                    // 用户聊天
                    msg = `
                        <div class="msg-left msg clear-float">
                            <div class="user-wrap clear-float">
                                <div class="header">
                                    <img class="user-img" src="${this.$store.state.friend.currentAvatar}" alt="NO IMG">
                                </div>
                                <div class="body">
                                    <div class="trangle user-trangle"></div>
                                    <div class="text user-text">
                                        ${data[2]}
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                } else if (data[1] === "1") {
                    // 群聊聊天
                    msg = `
                        <div class="msg-left msg clear-float">
                            <div class="name">${data[2]}</div>
                            <div class="user-wrap clear-float">
                                <div class="header">
                                    <img class="user-img" src="${data[3]}" alt="NO IMG">
                                </div>
                                <div class="body">
                                    <div class="trangle user-trangle"></div>
                                    <div class="text user-text">
                                        ${data[4]}
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                }
                this.$refs.chatPanelHook.innerHTML += msg;
                // 将页面调至最下面
                this.$refs.chatPanelHook.scrollTop = this.$refs.chatPanelHook.scrollHeight;
            };
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
                if (!sendId) {
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
                let removeFlag = new Date().getTime() + '';  // 移除等待标志
                sendType = sendType + '';
                if (sendType === '0') {
                    // 个人对个人发送消息
                    // 将该消息发送到自己的屏幕上
                    msgStr = `
                        <div class="msg-right msg clear-float">
                            <div class="user-wrap clear-float">
                                <div class="header">
                                    <img class="user-img" src="${this.userAvatar}" alt="NO IMG">
                                </div>
                                <div class="body">
                                    <div id="${removeFlag}" class="send-status">
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
                } else if (sendType === '1') {
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
                                    <div id="${removeFlag}" class="send-status">
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
                this.$refs.chatPanelHook.innerHTML += msgStr;    // 添加到聊天记录
                // 刷新div到底部显示
                this.$refs.chatPanelHook.scrollTop = this.$refs.chatPanelHook.scrollHeight;
                this.editor.html('');
                let sendStr = sendType === '0' ? "send_msgpersonnal" : "send_msggroup";
                chatApi.sendMessage(sendStr, getUser().id, sendId, msg, removeFlag).then((response) => {
                    if (response.data.flag) {
                        // 发送成功，移除等待标志
                        $(`#${response.data.data}`).remove();
                        // 将聊天对象添加到最近联系人
                        relationChatApi.deleteRelationChat(getUser().id, sendId).then((response) => {
                            if (response.data.flag) {
                                relationChatApi.addRelationChat(getUser().id, sendId, sendType).then((response) => {
                                    if (response.data.flag) {
                                        chatApi.getRelationChatList(getUser().id).then((response) => {
                                            let data = response.data.data;
                                            // 排序聊天记录
                                            data.sort((c1, c2) => {
                                                return c2.sort - c1.sort;
                                            });
                                            this.relationChatListData = data;
                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        this.$message({
                            message: response.data.message,
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
                    let sendType = this.$refs.sendBtnHook.dataset.sendtype;
                    let sendId = this.$refs.sendBtnHook.dataset.otherid;
                    // 发送消息
                    this.sendMsg(sendId, sendType);
                    return false;
                }
            },
            // 添加朋友
            addFriend() {
                this.$store.dispatch("friend/setaddFriendDialog", true);
            },
            // 添加群聊
            addGroup() {
                this.$store.dispatch("group/setaddGroupDialog", true);
            },
            // 展示个人信息
            personnalInfo() {
                userApi.getUserInfo(getUser().id).then((response) => {
                    let data = response.data.data;
                    // 计算年纪
                    let now = Date.parse(new Date());
                    let birth = Date.parse(data.birthday);
                    data["age"] = Math.floor(parseFloat((now - birth) / (1000 * 60 * 60 * 24 * 365)));
                    this.userInfo = data;
                    this.$store.dispatch("user/setpersonnalInfoDialog", true);
                    // 设置给store
                    this.$store.dispatch("user/setUserInfo", this.userInfo);
                });
            },
            // 管理分组
            manageGroup() {
                // 展示分组面板
                this.$store.dispatch("user/setUserGroupDialog", true);
                // 查询用户分组
                userApi.getGroupList(getUser().id).then((response) => {
                    this.$store.dispatch("user/setUserGroup", response.data.data);
                });
            },
            // 登出
            logout() {
                removeUser();
                location.href = "/login";
            },
            // 上传图片
            uploadImage(event) {
                let e = event || window.event;
                let currentDom = e.currentTarget;
                // 检查文件名
                let nameList = ["jpg", "jpeg", "png", "gif"];
                let nameArr = currentDom.files[0].name.split(".");
                let name = nameArr[nameArr.length - 1];
                if (nameList.indexOf(name) === -1) {
                    this.$message({
                        message: "上传文件不是图片",
                        type: "error",
                    });
                    return;
                }
                // 上传文件到后台
                let dict = new FormData();
                dict.append("image", currentDom.files[0]);
                chatApi.uploadImage(getUser().id, dict).then((response) => {
                    if (response.data.flag) {
                        // 上传成功
                        let bodyDom = $("iframe")[0].contentWindow.document.body;
                        $(bodyDom).append(`<img class="chat-image" src="${response.data.data}" alt="NO IMG" width="150">`);
                    } else {
                        // 上传失败
                        this.$message({
                            message: response.data.msg,
                            type: "error",
                        });
                    }
                });
            },
            // 上传文件
            uploadFile(event) {
                let sendId = this.$refs.sendBtnHook.dataset.otherid;
                if (!sendId) {
                    // 没有选择发送的对象
                    this.$message({
                        message: '请选择发送对象',
                        type: 'error'
                    });
                    return;
                }

                let e = event || window.event;
                let currentDom = e.currentTarget;
                // 上传文件到后台
                let dict = new FormData();
                dict.append("file", currentDom.files[0]);
                chatApi.uploadFile(getUser().id, dict).then((response) => {
                    if (response.data.flag) {
                        let removeFlag = new Date().getTime() + '';
                        // 上传成功
                        let msgStr = `
                            <div class="msg-right msg clear-float">
                                <div class="user-wrap clear-float">
                                    <div class="header">
                                        <img class="user-img" src="${this.userAvatar}" alt="NO IMG">
                                    </div>
                                    <div class="body">
                                        <div id="${removeFlag}" class="send-status">
                                            <i class="el-icon-loading"></i>
                                        </div>
                                        <div class="trangle user-trangle"></div>
                                        <div class="text user-text">
                                            <div class="chat-wrap">
                                                <div class="top">
                                                    <div class="left"><img src="/_nuxt/assets/images/cloud.png" alt="NO IMG"></div>
                                                    <div class="right">
                                                        <div class="file-name">${currentDom.files[0].name} (${currentDom.files[0].size}B)</div>
                                                        <div class="info"><i class="el-icon-success"></i>文件上传成功</div>
                                                    </div>
                                                </div>
                                                <div class="button"><a href="${response.data.data}">下载</a></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        `;
                        this.$refs.chatPanelHook.innerHTML += msgStr;    // 添加到聊天记录
                        // 刷新div到底部显示
                        this.$refs.chatPanelHook.scrollTop = this.$refs.chatPanelHook.scrollHeight;
                        // 保存到聊天记录
                        let file_msg = `
                            <div class="chat-wrap">
                                <div class="top">
                                    <div class="left"><img src="/_nuxt/assets/images/cloud.png" alt="NO IMG"></div>
                                    <div class="right">
                                        <div class="file-name">${currentDom.files[0].name} (${currentDom.files[0].size}B)</div>
                                        <div class="info"><i class="el-icon-success"></i>文件上传成功</div>
                                    </div>
                                </div>
                                <div class="button"><a href="${response.data.data}">下载</a></div>
                            </div>
                        `;
                        let sendType = this.$refs.sendBtnHook.dataset.sendtype;
                        let sendStr = sendType === '0' ? "send_msgpersonnal" : "send_msggroup";
                        chatApi.sendMessage(sendStr, getUser().id, sendId, file_msg, removeFlag).then((response) => {
                            // 移除等代标志
                            $(`#${response.data.data}`).remove();
                            // 将聊天对象添加到最近联系人
                            relationChatApi.deleteRelationChat(getUser().id, sendId).then((response) => {
                                if (response.data.flag) {
                                    relationChatApi.addRelationChat(getUser().id, sendId, sendType).then((response) => {
                                        if (response.data.flag) {
                                            chatApi.getRelationChatList(getUser().id).then((response) => {
                                                this.relationChatListData = response.data.data;
                                            });
                                        }
                                    });
                                }
                            });
                        });
                    } else {
                        // 上传失败
                        this.$message({
                            message: response.data.msg,
                            type: "error",
                        });
                    }
                });
            },
        },
        computed: {
            "isChatingComputed": function () {
                return this.$store.state.friend.currentNick;
            },
            "friendChatRecord": function () {
                return this.$store.state.friend.chatRecord;
            },
            "groupChatRecord": function () {
                return this.$store.state.friend.groupChatRecord;
            },
            "refreshFriendList": function () {
                return this.$store.state.user.refreshFriendList;
            },
            "refreshGroupList": function () {
                return this.$store.state.group.refreshGroupList;
            }
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
            // 好友聊天记录
            friendChatRecord: function () {
                // 清空聊天面板
                this.$refs.chatPanelHook.innerHTML = "";
                let msg = "";
                this.$store.state.friend.chatRecord.forEach((item) => {
                    let avatar = item.status === "left" ? this.$store.state.friend.currentAvatar : this.userAvatar;
                    msg += `
                        <div class="msg-${item.status} msg clear-float'"
                             v-if="$store.state.chat.sendType === 0">
                            <div class="user-wrap clear-float">
                                <div class="header">
                                    <img class="user-img" src="${avatar}" alt="NO IMG">
                                </div>
                                <div class="body">
                                    <div class="trangle user-trangle"></div>
                                    <div class="text user-text">
                                        ${item.message}
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                });
                this.$refs.chatPanelHook.innerHTML += msg;
                // 将页面调至最底端
                this.$refs.chatPanelHook.scrollTop = this.$refs.chatPanelHook.scrollHeight;
            },
            // 群聊天记录
            groupChatRecord: function () {
                // 清空聊天面板
                this.$refs.chatPanelHook.innerHTML = "";
                let msg = "";
                this.$store.state.friend.groupChatRecord.forEach((item) => {
                    let status = item.id === this.userId ? 'right' : 'left';
                    msg += `
                        <div class="msg-${status} msg clear-float">
                            <div class="name">${item.name}</div>
                            <div class="user-wrap clear-float">
                                <div class="header">
                                    <img class="user-img" src="${item.avatar}" alt="NO IMG">
                                </div>
                                <div class="body">
                                    <div class="trangle user-trangle"></div>
                                    <div class="text user-text">
                                        ${item.message}
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                });
                this.$refs.chatPanelHook.innerHTML += msg;
                // 将页面调至最底端
                this.$refs.chatPanelHook.scrollTop = this.$refs.chatPanelHook.scrollHeight;
            },
            // 在同意好友申请之后刷新好友列表
            refreshFriendList: function () {
                chatApi.getUserFriendList(getUser().id).then((response) => {
                    this.friendListData = response.data.data;
                });
            },
            // 在群主同意后从新刷新群组列表
            refreshGroupList: function () {
                chatApi.getUserGroupList(getUser().id).then((response) => {
                    this.groupListData = response.data.data;
                });
            }
        },
        components: {
            FriendList,
            GroupList,
            CurrentChat,
            RelationChat,
            AddFriend,
            AddGroup,
            PersonnalInfo,
            UserGroup,
            Notification,
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
                padding 0

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
                    margin 13px 0 0 15px

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
                        float right
                        background-color rgba(91, 188, 218, 0.19)
                        border-radius 18px
                        box-shadow 2px 2px 6px #d6d6d6
                        border 1px solid rgba(91, 188, 218, 0.29)
                        cursor pointer
                        color #575757

                        &:nth-child(3)
                            position relative
                            padding 5px 10px
                            margin 6px 10px

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


                        &:nth-child(2)
                            margin 6px 20px 0 5px
                            padding 11px 10px

                            i
                                margin-right 3px
                                font-size 16px

                        &:hover
                            background-color rgba(91, 188, 218, 0.29)

                        i
                            font-size 22px
                            vertical-align middle

                    .logout
                        float right
                        margin 5px 0
                        padding-top 6px

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
                            position relative
                            margin 0 auto
                            width 70px
                            height 70px
                            cursor pointer

                            &:hover
                                .group
                                    visibility visible

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
                .link-group
                    margin 20px 10px

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

</style>
