<template>
    <div class="box">
        <div class="dynamic-item" v-for="(item, index) in dynamicList" :key="index">
            <div class="head">
                <div class="img"><img :src="item.avatar" alt="NO IMG"></div>
                <div class="info">
                    <div class="nick">{{ item.nick }}</div>
                    <div class="time">{{ item.time }}</div>
                </div>
                <div class="delete" v-if="item.userId === userId">
                    <i class="el-icon-delete" title="删除动态"></i>
                </div>
            </div>
            <div class="content" v-html="item.content"></div>
            <div class="footer clear-float">
                <div class="share con"><i class="el-icon-share"></i></div>
                <div class="comment con"><i class="el-icon-edit-outline"></i></div>
                <div class="thumb-up con"><i class="el-icon-star-on"></i></div>
            </div>
            <div class="comment-input clear-float">
                <div style="float: left; width: 75%">
                    <el-input v-model="comment" placeholder="评论"></el-input>
                </div>
                <div style="float: right;">
                    <el-button type="primary" icon="el-icon-edit">评论</el-button>
                </div>
            </div>
            <div class="comment-list">

            </div>
        </div>
    </div>
</template>

<script>
    import {getUser} from "../../utils/auth";

    export default {
        props: {
            "dynamicList": {
                type: Array,
                default: [],
            },
        },
        data() {
            return {
                userId: "",
                comment: "",
            };
        },
        mounted() {
            this.userId = getUser().id;
        }
    }
</script>

<style scoped lang="stylus">
    .clear-float:after
        content ''
        display block
        clear both

    .box
        .dynamic-item
            background-color #fff
            padding 20px 15px
            margin-bottom 20px

            .head
                margin-bottom 5px
                width 100%
                height 60px

                .img
                    float left
                    width 60px
                    height 60px

                    img
                        margin 5px 0
                        width 50px
                        height 50px
                        border-radius 4px

                .info
                    float left

                    .nick
                        height 30px
                        line-height 30px

                    .time
                        height 30px
                        line-height 20px

                .delete
                    float right
                    height 60px
                    font-size 16px
                    line-height 60px

                    i
                        cursor pointer

                        &:hover
                            color #f00


            .footer
                padding-bottom 10px
                border-bottom 1px solid #dedede

                .con
                    width 50px
                    height 30px
                    font-size 20px
                    float right
                    text-align right

                    i
                        cursor pointer
                        color #575a60

                        &:hover
                            color #3988cc

            .comment-input
                margin-top 10px
</style>
