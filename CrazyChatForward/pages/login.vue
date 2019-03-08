<template>
    <div id="wrapper">
        <script>$(document).ready(function (c) {
            $('.close').on('click', function (c) {
                $('.login-form').fadeOut('slow', function (c) {
                    $('.login-form').remove();
                });
            });
        });
        </script>
        <!--SIGN UP-->
        <h1>CrazyChat 登录</h1>
        <div class="login-form">
            <div class="close"></div>
            <div class="head-info">
                <label class="lbl-1"> </label>
                <label class="lbl-2"> </label>
                <label class="lbl-3"> </label>
            </div>
            <div class="clear"></div>
            <!-- 头像 -->
            <div class="avtar">
                <img src="~/assets/images/avtar.png" alt="NO IMG"/>
            </div>
            <!-- 表单部分 -->
            <form>
                <div class="input-wrap">
                    <div class="trangle trangle-username" v-show="userNameInfo"></div>
                    <div class="err-info err-username" v-show="userNameInfo">{{ userNameInfo }}</div>
                    <input type="text" class="text" placeholder="User Name" v-model="userName">
                </div>

                <div class="key input-wrap">
                    <div class="trangle trangle-password" v-show="passwordInfo"></div>
                    <div class="err-info err-password" v-show="passwordInfo">{{ passwordInfo }}</div>
                    <input type="password" placeholder="password" v-model="password">
                </div>
            </form>
            <div class="forget-password">
                <p>还未注册账号?点击 <nuxt-link to="/register">注册</nuxt-link> 吧！</p>
            </div>
            <div class="signin">
                <input type="submit" value="Login" @click="login">
            </div>
        </div>
    </div>
</template>

<script>
    import userApi from "../api/user";
    import {setUser} from "../utils/auth";

    export default {
        data() {
            return {
                userName: "",
                password: "",
                userNameInfo: "",
                passwordInfo: "",
            };
        },
        methods: {
            // 登录账户
            login() {
                // 重置错误信息
                this.userNameInfo = "";
                this.passwordInfo = "";
                // 检查用户名跟密码不能为空
                if (this.userName === "") {
                    this.userNameInfo = "用户名不能为空！";
                    return;
                }
                if (this.password === "") {
                    this.passwordInfo = "密码不能为空！";
                    return;
                }
                // 通过验证，发送请求登录
                userApi.login(this.userName, this.password).then((response) => {
                    if (response.data.flag) {
                        // 登录成功
                        let data = response.data.data;
                        // 将用户数据存入cookie
                        setUser(data.id, data.token, data.nick, data.avatar)
                        // 跳转到首页

                    } else {
                        // 登录失败
                        this.$notify.error({
                            title: '登录失败',
                            message: response.data.message,
                        });
                    }
                });
            },
        },
    }
</script>

<style scoped lang="stylus">
    @import "../assets/css/style.css";

    .input-wrap
        position relative

        .trangle
            position absolute
            width 0
            height 0
            border-width 10px
            border-style solid
            border-color transparent #35ffff transparent transparent

        .trangle-username
            top 62px
            right 30px

        .trangle-password
            top 28px
            right 30px

        .err-info
            position absolute
            padding 10px 10px
            border-radius 8px
            color #f00
            font-size 16px
            box-shadow 1px 1px 8px #fff
            background-color #35ffff

        .err-username
            top 54px
            right -118px

        .err-password
            top 20px
            right -101px
</style>

