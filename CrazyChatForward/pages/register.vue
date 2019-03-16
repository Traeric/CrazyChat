<template>
    <div id="wrap">
        <div class="bg"></div>
        <div class="box">
            <div class="header">
                <div class="header-wrap">
                    <img src="~/assets/images/avtar.png" alt="NO IMG">
                </div>
            </div>
            <div class="title">
                欢迎注册CrazyChat账号
            </div>
            <div class="alert alert-danger" role="alert" v-show="errMsg">{{ errMsg }}</div>
            <div class="form-area">
                <div>
                    <div class="form-group">
                        <label for="exampleInputUsername">用户名</label>
                        <input type="text" class="form-control" id="exampleInputUsername"
                               v-model="registerData.name"
                               placeholder="username">
                    </div>
                    <div class="form-group">
                        <label for="exampleInputEmail">邮箱</label>
                        <input type="email" class="form-control" id="exampleInputEmail"
                               v-model="registerData.email"
                               placeholder="email">
                    </div>
                    <div class="form-group">
                        <label for="exampleInputPassword">密码</label>
                        <input type="password" class="form-control" id="exampleInputPassword"
                               v-model="registerData.password"
                               placeholder="password">
                    </div>
                    <div class="form-group">
                        <label for="exampleInputRePassword">确认密码</label>
                        <input type="password" class="form-control" id="exampleInputRePassword"
                               v-model="rePassword"
                               placeholder="re-password">
                    </div>
                    <label for="exampleInputCode">验证码</label>
                    <div class="form-inline">
                        <input type="text" maxlength="4" class="form-control" id="exampleInputcode"
                               v-model="confirmCode"
                               placeholder="code">
                        <button type="button" class="btn btn-primary" @click="getConfirmCode">获取验证码</button>
                    </div>
                    <div style="margin-top: 20px;">
                        <button type="button" @click="register" class="btn btn-success btn-block">注册</button>
                    </div>
                </div>
            </div>
            <div class="to-login">
                <button type="button" class="btn btn-default btn-lg btn-block" @click="toLogin">
                    已有账号? 去登陆
                    <i class="el-icon-back" style="transform: rotate(180deg);"></i>
                </button>
            </div>
            <div class="friend-links">
                <nuxt-link to="/chat_room">聊天室</nuxt-link>
            </div>
        </div>
    </div>
</template>

<script>
    import "~/assets/css/bootstrap.min.css";
    import userApi from "~/api/user";

    export default {
        data() {
            return {
                registerData: {
                    name: "",
                    email: "",
                    password: "",
                },
                confirmCode: "",
                rePassword: "",
                errMsg: "",
            };
        },
        methods: {
            // 注册
            register() {
                this.errMsg = "";
                // 验证数据是否为空
                if (this.registerData.name === "") {
                    this.errMsg = "必须填写用户名~~";
                    return;
                }
                if (this.registerData.email === "") {
                    this.errMsg = "邮箱不能为空哦~~";
                    return;
                }
                if (this.registerData.password === "") {
                    this.errMsg = "密码必须填写~~";
                    return;
                }
                if (this.confirmCode === "") {
                    this.errMsg = "验证码不能为空~~";
                    return;
                }
                // 验证两次密码是否填写正确
                if (this.registerData.password !== this.rePassword) {
                    this.errMsg = "两次密码填写不一致，请重新填写";
                    this.rePassword = "";
                    return;
                }
                // 全部正确，发送注册信息
                userApi.register(this.confirmCode, this.registerData).then((response) => {
                    if (response.data.flag) {
                        // 注册成功，跳转到登录页
                        this.$router.push("/login");
                    } else {
                        // 注册失败
                        this.$message({
                            message: response.data.message,
                            type: 'error'
                        });
                    }
                });
            },
            // 获取注册码
            getConfirmCode() {
                // 获取邮箱
                let reg = /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/gi;
                if (!reg.test(this.registerData.email)) {
                    // 邮箱不正确
                    this.errMsg = "邮箱格式不正确";
                    return;
                }
                // 获取验证码
                userApi.getConfirmCode(this.registerData.email).then((response) => {
                    this.$notify({
                        title: (response.data.flag ? "成功" : "失败"),
                        message: response.data.message,
                        type: (response.data.flag ? "success" : "error"),
                    });
                });
            },
            toLogin() {
                this.$router.push("/login");
            }
        },
    }
</script>

<style scoped lang="stylus">
    #wrap
        .bg
            position fixed
            top 0
            left 0
            right 0
            bottom 0
            background-color #f9f9f9
            z-index -1
        .box
            margin 100px auto
            width 350px

        .header
            width 100%
            height 60px

            .header-wrap
                margin 0 auto
                width 60px
                height 60px

                img
                    width 100%
                    height 100%
                    border-radius 50%

        .alert
            margin-bottom 0
            padding 8px 0
            text-align center

        .title
            width 100%
            height 60px
            line-height 60px
            text-align center
            font-weight 700
            font-size 22px

        .form-area
            padding 30px 20px
            border-radius 8px
            background-color #fff
            border 1px solid #d8dee2
        .to-login
            margin-top 20px
        .friend-links
            margin-top 20px

</style>
