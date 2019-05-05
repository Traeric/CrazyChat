<template xmlns:v-quill="http://www.w3.org/1999/xhtml">
    <div id="wrap">
        <div class="nav">
            <div class="cloud"><img src="~/assets/images/cloud.png" alt="NO IMG"></div>
            <div class="title">
                <div class="title-text"></div>
            </div>
            <div class="user">
                <!-- 用户信息区 -->
                <!-- 未登录 -->
                <div class="logout" v-if="isLogin === false">
                    <el-row>
                        <label>登录</label>
                        <el-button title="登录" type="success" icon="el-icon-news" circle @click="toLogin"></el-button>
                        <label>注册</label>
                        <el-button title="注册" type="warning" icon="el-icon-edit" circle @click="toRegister"></el-button>
                    </el-row>
                </div>
                <!-- 已登陆 -->
                <div class="login" v-if="isLogin">
                    <div class="avatar"><img :src="avatar" alt="NO IMG"></div>
                    <div class="nick">{{ userName }}</div>
                </div>
            </div>
            <div class="links">
                <!-- 链接区 -->
                <el-row>
                    <el-button type="text" style="color: #fff; font-weight: 700; cursor: default; font-size: 18px;">
                        友情链接：
                    </el-button>
                    <el-button type="text">
                        <nuxt-link to="/home_page">个人主页</nuxt-link>
                    </el-button>
                    <el-button type="text">
                        <nuxt-link to="/dynamic_space">动态空间</nuxt-link>
                    </el-button>
                </el-row>
            </div>
        </div>
        <div class="body">
            <div class="col-md-2 col-lg-2 left">
                <!-- 左侧 -->
                <div class="title">在线游客列表
                    <small>在线人数({{ onlineUser }})</small>
                </div>
                <div class="left-area" ref="leftHook"></div>
            </div>
            <div class="middle col-md-8 col-lg-8">
                <!-- 聊天区域 -->
                <div class="show-msg" ref="showMsgHook">
                </div>
                <div class="send-msg" ref="sendMsgHook">
                    <div class="edit">
                        <div class="quill-editor"
                             @change="onEditorChange($event)"
                             :content="content"
                             v-quill:myQuillEditor="editorOption">
                        </div>
                    </div>
                    <div class="send-btn">
                        <button type="button" class="btn btn-default close-btn">关闭</button>
                        <button type="button" class="btn btn-info send-msg-btn" @click="sendMsg">发送</button>
                    </div>
                </div>
            </div>
            <div class="right col-md-2 col-lg-2">
                <!-- 右侧 -->
                <div class="top">
                    <div class="title">不知道写什么</div>
                    <div class="area"></div>
                </div>
                <div class="bottom">
                    <div class="title">不知道写什么2</div>
                    <div class="area"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import "~/assets/css/bootstrap.min.css";
    import "~/assets/css/chat.css";
    import {getUser} from "../../utils/auth";

    export default {
        data() {
            return {
                isLogin: true,
                content: "",
                userName: "",
                avatar: "",
                editorOption: {
                    // some quill options
                    modules: {
                        toolbar: [
                            ['bold', 'underline'],
                            // ['bold', 'underline', 'image'],
                        ]
                    }
                },
                ws: null,
                onlineUser: 0,
            };
        },
        mounted() {
            // 检查用户是否登录
            if (getUser().id) {
                this.isLogin = true;
                // 设置用户的名字跟头像
                this.userName = getUser().nick;
                this.avatar = getUser().avatar;
            } else {
                this.isLogin = false;
            }
            // 设置聊天室聊天部分的高度
            let windowHeight = window.innerHeight - 65;
            this.$refs.showMsgHook.style.height = `${windowHeight * 0.75}px`;
            this.$refs.sendMsgHook.style.height = `${windowHeight * 0.25}px`;

            // 建立socket连接
            this.ws = new WebSocket("ws://127.0.0.1:9001/chat_room");

            this.ws.onmessage = (event) => {
                // 接收到消息
                if (event.data.startsWith("@#$zw")) {
                    // 表示系统的通知消息
                    let dataList = event.data.split(" ");
                    if (dataList[2] === "i") {
                        // 有人进入聊天室
                        if (dataList[5] !== "self") {
                            // 通知其他人
                            this.$message({
                                message: dataList[1] + "进入聊天室",
                                type: 'success'
                            });
                            // 对右侧的游客列表进行刷新
                            let imageUrl = require(`~/assets/images/chatroom/zw${dataList[4]}.png`);
                            this.$refs.leftHook.innerHTML += `
                                <div class="user ${dataList[1]}">
                                    <div class="img"><img src="${imageUrl}" alt="NO IMG"></div>
                                    <div class="name">${dataList[1]}</div>
                                </div>
                            `;
                        } else {
                            // 自己进入聊天室
                            let userList = dataList[6].split("|");
                            for (let item of userList) {
                                if (item) {
                                    // 获取游客名和头像
                                    let visitorInfo = item.split("&");
                                    let imageUrl = require(`~/assets/images/chatroom/zw${visitorInfo[1]}.png`);
                                    this.$refs.leftHook.innerHTML += `
                                        <div class="user ${visitorInfo[0]}">
                                            <div class="img"><img src="${imageUrl}" alt="NO IMG"></div>
                                            <div class="name">${visitorInfo[0]}</div>
                                        </div>
                                    `;
                                }
                            }
                        }
                    } else {
                        // 有人退出聊天室
                        this.$message({
                            message: dataList[1] + "退出聊天室",
                            type: 'error'
                        });
                        // 在左侧列表中删除该游客
                        $(`.${dataList[1]}`).remove();
                    }
                    // 更新在线人数
                    this.onlineUser = dataList[3];
                } else {
                    // 用户聊天消息
                    this.$refs.showMsgHook.innerHTML += event.data;
                }
            };
        },
        methods: {
            toLogin() {
                this.$router.push("/login");
            },
            toRegister() {
                this.$router.push("/register");
            },
            onEditorChange({editor, html, text}) {
                this.content = html;
            },
            // 发送消息
            sendMsg() {
                if (this.content.trim() === "") {
                    // 空消息
                    this.$message({
                        message: '不能发送空消息',
                        type: 'info'
                    });
                    return;
                }
                // 发送消息
                this.ws.send(this.content.replace("<p>", "").replace("</p>", ""));
                this.content = "";      // 清空消息
            },
        },
        components: {},
    }
</script>

<style scoped lang="stylus">
    .clear-float::after
        content ""
        display block
        clear both

    #wrap
        .nav
            position relative
            width 100%
            height 60px
            background-color #85d5fc
            border-radius 0 0 10px 10px

            .cloud
                position absolute
                z-index 10000
                top 10px
                left 15px
                width 150px

                img
                    width 100%

            .title
                position absolute
                width 100%
                height 60px

                .title-text
                    margin 12px auto !important
                    width 470px
                    height 36px
                    background-image url("../../assets/images/title.PNG")
                    background-size 470px 36px

            .links
                float right
                margin-right 40px
                height 60px
                line-height 60px

                a
                    text-decoration none
                    color #fff

                    &:hover
                        color #e2e2e2

            .user
                float right

            .logout
                margin 10px 20px 0 0

                label
                    margin 0 3px 0 10px
                    color #fff
                    font-weight 700
                    text-shadow 2px 2px 4px #595959

            .login
                div
                    float right

                .avatar
                    margin-right 20px
                    width 60px
                    height 100%

                    img
                        margin 5px
                        width 50px
                        height 50px
                        border-radius 50%

                .nick
                    height 60px
                    line-height 60px
                    margin-right 10px
                    text-shadow 2px 2px 6px #6a6a6a
                    color #f2f2f2
                    font-weight 700
                    font-size 18px

        .body
            .left
                padding 0
                margin-top 54px
                border-radius 8px
                box-shadow 4px 4px 12px #5f5f5f
                background-color #f9f9f9

                .left-area
                    width 100%
                    height 770px
                    overflow auto

                .title
                    height 50px
                    line-height 50px
                    border-radius 8px 8px 0 0
                    background-color #85d5fc
                    text-align center
                    color #fff
                    font-weight 700
                    font-size 22px
                    text-shadow 2px 2px 6px #6a6a6a

            .middle
                margin-top 5px

                .show-msg
                    border 1px solid #cbcbcb
                    border-radius 12px 12px 0 0
                    background-color #f9f9f9
                    padding 20px 30px
                    overflow auto

        .send-msg
            .edit
                height 70%

                .quill-editor
                    overflow-y auto

            .send-btn
                position relative
                height 30%
                background-color #f4f4f4

                button
                    position absolute
                    top 10px

                .close-btn
                    right 84px

                .send-msg-btn
                    right 18px

        .right
            margin-top 5px

            .title
                border-radius 8px 8px 0 0
                height 50px
                line-height 50px
                text-align center
                color #fff
                font-weight 700
                font-size 22px
                text-shadow 2px 2px 6px #6a6a6a

            .top
                background-color #f0f0f0
                border-radius 8px

                .title
                    background-color #b2e281

                .area
                    min-height 400px

            .bottom
                background-color #f0f0f0
                border-radius 8px

                .title
                    background-color #b2e281

                .area
                    min-height 400px
</style>
