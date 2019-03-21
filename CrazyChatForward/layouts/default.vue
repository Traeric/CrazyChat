<template>
    <div>
        <nuxt/>
    </div>
</template>

<style>
    /* 设置滚动条的样式 */
    ::-webkit-scrollbar {
        width: 6px;
    }

    /* 滚动槽 */
    ::-webkit-scrollbar-track {
        background-color: rgb(239, 239, 239);
        border-radius: 30px;
    }

    /* 滚动条滑块 */
    ::-webkit-scrollbar-thumb {
        border-radius: 30px;
        -webkit-box-shadow: inset 0 0 6px #a4a4a4;
    }
</style>

