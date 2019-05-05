<template>
    <div id="wrapper">
        <div class="nav">
            <div class="inner">
                <ul class="nav-list clear-float">
                    <li class="logo">
                        <img src="../../assets/images/dynamic/logo.png" alt="NO IMG">
                        Eric Jin空间
                    </li>
                    <li>
                        <i class="glyphicon glyphicon-user"></i>
                        个人主页
                    </li>
                    <li>
                        <i class="glyphicon glyphicon-comment"></i>
                        聊天室
                    </li>
                    <li>
                        <i class="el-icon-menu"></i>
                        皮肤
                    </li>
                </ul>
                <ul class="right-list clear-float">
                    <li class="avatar"><img :src="userAvatar" alt="NO IMG"></li>
                    <li class="nick">{{ userName }}</li>
                    <li title="退出登陆" class="quit"><i class="glyphicon glyphicon-log-out"></i></li>
                </ul>
            </div>
        </div>
        <div class="banner">
            <div class="center-content">
                <div class="title">我的动态空间 <i class="el-icon-upload"></i></div>
                <div class="user-info">
                    <img :src="userAvatar" alt="NO IMG">
                    <div class="name">{{ userName }}</div>
                </div>
            </div>
        </div>
        <div class="content">
            <div class="content-banner">
                <div class="center">
                    <ul class="center-list">
                        <li>主页</li>
                        <li>动态</li>
                    </ul>
                </div>
            </div>
            <div class="cotent-box">
                <div class="left">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title">个人档案</h3>
                        </div>
                        <div class="panel-body">
                            <div class="alert alert-success" role="alert">昵称：{{ userName }}</div>
                            <div class="alert alert-warning" role="alert">邮箱：{{ userInfo.email }}</div>
                            <div class="alert alert-danger" role="alert">生日：{{ userInfo.birthday }}</div>
                            <div class="alert alert-info" role="alert">签名：{{ userInfo.sign }}</div>
                        </div>
                    </div>
                </div>
                <div class="right">
                    <DynamicLoad :dynamicList="dynamicList" />
                </div>
            </div>
        </div>

        <!-- 背景 -->
        <div class="bg"></div>
    </div>
</template>

<script>
    import "~/assets/css/bootstrap.min.css";
    import {getUser} from "../../utils/auth";
    import userApi from "../../api/user";
    import dynamicApi from "../../api/dynamic";
    import DynamicLoad from "../../components/dynamicLoad/dynamicLoad";

    export default {
        data() {
            return {
                userName: "",
                userAvatar: "",
                userId: "",
                userInfo: {},
                dynamicList: [],
            };
        },
        mounted() {
            // 初始化用户信息
            this.userName = getUser().nick;
            this.userAvatar = getUser().avatar;
            this.userId = getUser().id;
            userApi.getUserInfo(getUser().id).then((response) => {
                this.userInfo = response.data.data;
            });

            // 加载动态
            dynamicApi.loadDynamicPerson(getUser().id).then((response) => {
                this.dynamicList = response.data.data;
            });
        },
        components: {
            DynamicLoad,
        },
    }
</script>

<style scoped lang="stylus">
    .clear-float::after
        content ''
        display block
        clear both

    #wrapper
        .nav
            width 100%
            height 40px
            background-color #000

            .inner
                width 65%
                height 100%
                margin 0 auto
                color #fff

                .nav-list
                    margin-top 0 !important
                    float left

                    li
                        float left
                        margin 0 15px
                        height 100%
                        line-height 40px
                        font-size 14px
                        cursor pointer

                        &:hover
                            color #dfdfdf

                        i
                            font-size 16px

                    .logo
                        margin 0 20px 0 0
                        height 100%
                        line-height 40px
                        user-select none
                        font-size 16px

                        &:hover
                            color #fff

                        img
                            margin 7px
                            width 26px
                            height 26px
                            border-radius 50%
                            float left

                .right-list
                    float right

                    li
                        float left

                    .avatar
                        img
                            margin 5px
                            width 30px
                            height 30px
                            border-radius 4px

                    .nick
                        padding 0 10px
                        height 100%
                        line-height 40px

                    .quit
                        height 100%
                        line-height 43px
                        color #f00
                        font-size 16px

                        i
                            cursor pointer


        .banner
            width 100%
            height 300px
            background-image url("../../assets/images/dynamic/banner.jpg")
            background-size 100% 300px

            .center-content
                position relative
                margin 0 auto
                width 65%
                height 100%

                .title
                    position absolute
                    top 20px
                    font-size 30px
                    color #fff

                .user-info
                    position absolute
                    bottom -30px

                    img
                        float left
                        height 150px
                        width 150px
                        border-radius 4px
                        border 5px solid #d9dfee

                    .name
                        float left
                        margin-left 20px
                        font-size 22px
                        color #fff

        .content
            margin-top -8px

            .content-banner
                width 100%
                height 50px
                background-color #fff

                .center
                    margin 0 auto
                    padding-left 180px
                    width 65%
                    height 100%

                    .center-list
                        li
                            margin 0 20px
                            height 50px
                            line-height 50px
                            float left
                            cursor pointer

                            &:hover
                                color: #f90

            .cotent-box
                display flex
                margin 30px auto 0
                width 50%

                .left
                    margin-right 20px
                    flex 0 0 350px
                    height 100%

                .right
                    flex 1
                    margin-left 20px
                    height 100%

        .bg
            position fixed
            top 0
            bottom 0
            left 0
            right 0
            z-index -1
            background-color #f4f4f4
</style>
