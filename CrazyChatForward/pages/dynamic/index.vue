<template>
    <div class="wrap">
        <div class="dynamic-box">
            <div class="top">
                <div class="img">
                    <img src="~/assets/images/chatroom/zw1.png" alt="NO IMG">
                </div>
                <div class="nick">金千亿</div>
                <div class="only">
                    <el-switch
                        v-model="onlyme"
                        active-text="仅自己可见">
                    </el-switch>
                </div>
                <div style="clear: both;"></div>
            </div>
            <div class="middle">
                <div class="text" contenteditable="true">
                    说点什么吧
                </div>
                <div class="picture">
                    <div class="display-area clear-float">
                        <div class="tips">
                            <i class="el-icon-picture"></i>
                            图片（2/9）
                        </div>
                        <span class="img-display">
                            <div class="item"><span class="delete el-icon-close"></span><img src="~/assets/images/bg1.jpg" alt="NO IMG"></div>
                            <div class="item"><span class="delete el-icon-close"></span><img src="~/assets/images/bg1.jpg" alt="NO IMG"></div>
                            <div class="item"><span class="delete el-icon-close"></span><img src="~/assets/images/bg1.jpg" alt="NO IMG"></div>
                            <div class="item"><span class="delete el-icon-close"></span><img src="~/assets/images/bg1.jpg" alt="NO IMG"></div>
                            <div class="item"><span class="delete el-icon-close"></span><img src="~/assets/images/bg1.jpg" alt="NO IMG"></div>
                            <div class="item"><span class="delete el-icon-close"></span><img src="~/assets/images/bg1.jpg" alt="NO IMG"></div>
                        </span>
                        <div class="item img-icon">
                            <input type="file" title="上传图片">
                            <i class="el-icon-picture-outline"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="footer">
                <div class="see-icon">
                    <span>
                        <i class="el-icon-view"></i>
                        对谁可见
                    </span>
                </div>
                <div class="see-area"></div>
                <div class="send-btn">
                    <el-button type="info" plain icon="el-icon-error">关闭</el-button>
                    <el-button type="success" plain icon="el-icon-upload">发布动态</el-button>
                </div>
            </div>
        </div>


        <div class="bg"></div>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                onlyme: false,
            };
        },
    }
</script>

<style scoped lang="stylus">
    .clear-float::after
        content ''
        display block
        clear both

    .wrap
        .dynamic-box
            width 700px
            background-color #fff
            margin 50px auto
            border-radius 4px

            .top
                .img
                    float left
                    width 60px
                    height 60px

                    img
                        margin 5px
                        width 50px
                        height 50px
                        border-radius 12px

                .nick
                    float left
                    margin-left 10px
                    height 60px
                    text-align center
                    line-height 60px

                .only
                    float right
                    margin-right 20px
                    height 60px
                    line-height 60px

            .middle
                margin-top 10px

                .text
                    margin 0 2px
                    padding 10px
                    height 200px
                    border 1px solid #dedede
                    outline none

                .picture
                    .display-area
                        padding-left 42px
                        border-bottom 1px solid #adadad

                        .tips
                            margin-left -30px
                            height 30px
                            line-height 30px

                        .item
                            position relative
                            margin 5px 2px
                            float left
                            width 150px
                            height 150px
                            overflow hidden
                            border-radius 4px
                            .delete
                                position absolute
                                top 0
                                right 0
                                padding 5px
                                background-color rgba(0, 0, 0, .5)
                                cursor pointer
                                border-radius 2px
                                color #fff
                                &:hover
                                    background-color rgba(0, 0, 0, .7)
                        .img-icon
                            position relative
                            border 1px dotted #747474
                            text-align center
                            line-height 212px
                            cursor pointer
                            i
                                font-size 100px

                            &:hover
                                background-color #f9f9f9

                                i
                                    color #696969
                            input
                                position absolute
                                top 0
                                left 0
                                outline none
                                opacity 0
                                cursor pointer
                                width 150px
                                height 150px

            .footer
                .see-icon
                    padding-right 10px
                    height 30px
                    line-height 30px
                    text-align right

                    span
                        cursor pointer
                        color #f90

                        &:hover
                            color #ff0833

                .send-btn
                    border-top 1px solid #a7a7a7
                    padding-right 20px
                    height 70px
                    line-height 70px
                    text-align right

        .bg
            position fixed
            top 0
            bottom 0
            left 0
            right 0
            z-index -100
            background-image url("../../assets/images/dynamic/bg1.jpg")
</style>
