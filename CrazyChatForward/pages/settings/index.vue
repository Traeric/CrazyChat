<template>
    <div class="wrap">
        <div class="nav">
            <div class="left col-md-1 col-lg-1 col-sm-1">
                <span @click="back">
                    <i class="el-icon-back"></i>
                    返回
                </span>
            </div>
            <div class="middle col-md-10 col-lg-10 col-sm-10">
                用户设置页
            </div>
            <div class="right col-md-1 col-lg-1 col-sm-1"></div>
        </div>
        <div class="body">
            <div class="left col-md-1 col-lg-1 col-sm-1"></div>
            <div class="middle clear-float col-md-10 col-lg-10 col-sm-10">
                <div class="user-account col-md-4 col-lg-4 col-sm-4">
                    <div class="topic">用户账户设置</div>
                    <div class="form">
                        <el-form ref="form" label-width="80px">
                            <el-form-item label="用户昵称">
                                <el-input style="width: 300px;" v-model="nick"></el-input>
                                <button type="button" class="btn btn-link" @click="profileNick" style="outline: none;">
                                    修改
                                </button>
                            </el-form-item>

                            <el-form-item label="邮箱">
                                <el-input style="width: 300px;" v-model="email"></el-input>
                                <button type="button" class="btn btn-link" @click="profileEmail" style="outline: none;">
                                    修改
                                </button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
                <div class="user-info col-md-4 col-lg-4 col-sm-4">
                    <div class="topic">用户信息设置</div>
                    <div class="form">
                        <el-form ref="form" label-width="80px">
                            <el-form-item label="签名">
                                <el-input v-model="userInfoObj.sign"></el-input>
                            </el-form-item>

                            <el-form-item label="性别">
                                <el-radio-group v-model="userInfoObj.gender">
                                    <el-radio label="0">男</el-radio>
                                    <el-radio label="1">女</el-radio>
                                </el-radio-group>
                            </el-form-item>

                            <el-form-item label="生日">
                                <el-date-picker type="date" placeholder="选择日期"
                                                v-model="userInfoObj.birthday"></el-date-picker>
                            </el-form-item>

                            <el-form-item label="个人简介">
                                <el-input type="textarea" v-model="userInfoObj.describe"></el-input>
                            </el-form-item>

                            <el-form-item>
                                <el-button type="warning" style="float: right;" @click="profileInfo">保存</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
                <div class="user-appearance col-md-4 col-lg-4 col-sm-4">
                    <div class="topic">用户外观设置</div>
                    <div class="form">
                        <div class="img">
                            <div class="title">用户头像</div>
                            <div>
                                <img :src="avatar" alt="NO IMG" class="img-thumbnail" ref="imgHook">
                                <div class="change-img" @click="profileAvatar">
                                    <input type="file" name="img" accept="image/*" @change="profileAvatar"
                                           ref="inputHook">
                                    修改图片
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="right col-md-1 col-lg-1 col-sm-1"></div>
        </div>
        <div class="bg"></div>
    </div>
</template>

<script>
    import {getUser} from "../../utils/auth";
    import "../../assets/css/bootstrap.min.css"
    import userApi from "../../api/user";
    import Cookie from "js-cookie";

    export default {
        data() {
            return {
                userInfoObj: {
                    sign: "",
                    gender: "",
                    birthday: "",
                    describe: "",
                },
                avatar: "",
                nick: "",
                email: "",
            };
        },
        created() {
            // 判断登录
            if (!getUser().id) {
                // 未登录
                this.$router.push("/login");
                return;
            }
            userApi.getUserInfo(getUser().id).then((response) => {
                let data = response.data.data;
                this.userInfoObj.sign = data.sign;
                this.userInfoObj.gender = data.gender;
                this.userInfoObj.birthday = data.birthday;
                this.userInfoObj.describe = data.describe;
                this.nick = data.nick;
                this.email = data.email;
                this.avatar = getUser().avatar;
            });
        },
        methods: {
            back() {
                window.history.back();
            },
            // 修改昵称
            profileNick() {
                this.$prompt('请输入新昵称', '修改昵称', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPattern: /^[\u4e00-\u9fa5_a-zA-Z0-9]+$/,
                    inputErrorMessage: '昵称格式不正确'
                }).then(({value}) => {
                    // 确定输入
                    userApi.profileNick(getUser().id, value).then((response) => {
                        this.$notify({
                            title: (response.data.flag ? "成功" : "失败"),
                            message: response.data.message,
                            type: (response.data.flag ? "success" : "error"),
                        });
                        if (response.data.flag) {
                            this.nick = value;
                            Cookie.set("userNick", value);
                        }
                    });
                });
            },
            // 修改邮件
            profileEmail() {
                this.$prompt('请输入新邮件', '修改邮件', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPattern: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
                    inputErrorMessage: '邮箱格式不正确'
                }).then(({value}) => {
                    if (value === this.email) {
                        this.$message({
                            message: "不能使用同样的邮箱",
                            type: "error",
                        });
                        return;
                    }
                    // 确定输入
                    userApi.profileEmail(getUser().id, value).then((response) => {
                        this.$notify({
                            title: (response.data.flag ? "成功" : "失败"),
                            message: response.data.message,
                            type: (response.data.flag ? "success" : "error"),
                        });
                    });
                });
            },
            // 修改用户信息
            profileInfo() {
                userApi.profileInfo(getUser().id, this.userInfoObj).then((response) => {
                    this.$notify({
                        title: (response.data.flag ? "成功" : "失败"),
                        message: response.data.message,
                        type: (response.data.flag ? "success" : "error"),
                    });
                });
            },
            // 修改头像
            profileAvatar() {
                // 获取上传的图片文件
                if (this.$refs.inputHook.files[0]) {
                    // 上传图片
                    let dict = new FormData();
                    dict.append("avatar", this.$refs.inputHook.files[0]);
                    /**
                     * 检查上传文件是否是图片
                     */
                    let name = this.$refs.inputHook.files[0].name.split(".")[1];
                    let size = this.$refs.inputHook.files[0].size;
                    // 检查大小
                    if (size > 1024 * 1024 * 10) {   // 不能超过10MB
                        this.$message({
                            message: "图片大小不能超过10MB",
                            type: "error",
                        });
                        return;
                    }
                    // 检查文件名
                    let nameList = ["jpg", "jpeg", "png", "gif"];
                    if (nameList.indexOf(name) === -1) {
                        this.$message({
                            message: "上传文件不是图片",
                            type: "error",
                        });
                        return;
                    }
                    userApi.profileAvatar(getUser().id, dict).then((response) => {
                        this.$notify({
                            title: (response.data.flag ? "成功" : "失败"),
                            message: response.data.message,
                            type: (response.data.flag ? "success" : "error"),
                        });
                        // 设置用户头像回显
                        if (response.data.flag) {
                            this.$refs.imgHook.src = window.URL.createObjectURL(this.$refs.inputHook.files[0]);
                        }
                        // 修改cookie里面的avatar
                        Cookie.set("userAvatar", response.data.data);
                    });
                }
            },
        },
    }
</script>

<style scoped lang="stylus">
    .clear-float::after
        content ''
        display block
        clear both

    .wrap
        .bg
            position fixed
            top 0
            left 0
            right 0
            bottom 0
            background-color #f9f9f9
            z-index -1

        .nav
            height 60px

            .left
                padding-left 20px
                line-height 60px
                font-weight bolder
                font-size 22px

                span
                    cursor pointer
                    user-select none
                    color #b2e281

                    &:hover
                        color #97c06d

            .middle
                height 60px
                line-height 60px
                font-weight 700
                font-size 24px
                color #fff
                text-shadow 2px 2px 6px #828282
                text-align center

        .body
            margin-top 20px

            .middle
                div
                    .topic
                        text-align center
                        border-radius 8px
                        font-weight 700
                        height 40px
                        line-height 40px

                .user-account
                    .topic
                        background-color #eaeaea
                        color #555

                    .form
                        margin-top 30px

                .user-info
                    .topic
                        background-color #9bb9dc
                        color #ececec

                    .form
                        margin-top 30px

                .user-appearance
                    .topic
                        background-color #7ec9ee
                        color #ececec

                    .form
                        .img

                            .title
                                padding-left 20px
                                margin 0 0 20px 0
                                height 40px
                                line-height 40px
                                font-size 18px
                                font-weight 700

                            div
                                position relative
                                width 200px
                                height 200px
                                margin-left 60px
                                overflow hidden

                                &:hover
                                    .change-img
                                        bottom 0

                                img
                                    width 200px
                                    height 200px

                                .change-img
                                    position absolute
                                    bottom -40px
                                    margin 0
                                    transition bottom .4s ease
                                    border-radius 0 0 4px 4px
                                    height 40px
                                    width 200px
                                    line-height 40px
                                    text-align center
                                    cursor pointer
                                    user-select none
                                    color #fff
                                    background-color rgba(0, 0, 0, .4)

                                    &:hover
                                        background-color rgba(0, 0, 0, .5)

                                    input
                                        position absolute
                                        width 200px
                                        height 40px
                                        cursor pointer
                                        opacity 0
</style>
