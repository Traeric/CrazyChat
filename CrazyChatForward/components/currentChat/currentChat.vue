<template>
    <div class="info clear-float">
        <div class="img"><img :src="avatar" alt="NO IMG"></div>
        <div class="nick">{{ nick }}</div>
    </div>
</template>

<script>
    export default {
        props: {
            "nick": {
                type: String,
                default: "",
            },
            "avatar": {
                type: String,
                default: "",
            },
        },
    }
</script>

<style scoped lang="stylus">
    .info
        height 40px

        .img
            float left
            width 40px
            height 40px

            img
                width 100%
                height 100%
                border-radius 12px

        .nick
            margin-left 10px
            float left
            line-height 40px
</style>
