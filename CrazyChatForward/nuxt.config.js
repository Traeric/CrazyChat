module.exports = {
    /*
    ** Headers of the page
    */
    head: {
        title: 'Carzy Chat',
        meta: [
            {charset: 'utf-8'},
            {name: 'viewport', content: 'width=device-width, initial-scale=1'},
            {hid: 'description', name: 'description', content: 'Nuxt.js project'}
        ],
        link: [
            {rel: 'icon', type: 'image/x-icon', href: '/icon.ico'},
            {rel: 'stylesheet', href: 'http://at.alicdn.com/t/font_1069362_h4wym2id4gs.css'},
        ],
        script: [
            {src: 'js/jquery-3.3.1.min.js'},
            {src: 'js/kindeditor/kindeditor-all.js'},
        ],
    },
    css: [
        "~/assets/css/reset.css",
        'element-ui/lib/theme-chalk/index.css',
        // quill-editor
        'quill/dist/quill.snow.css',
        'quill/dist/quill.bubble.css',
        'quill/dist/quill.core.css',
        '~/static/js/kindeditor/themes/zw/zw.css',
    ],
    plugins: [
        // element ui
        { src: "~/plugins/element-ui.js", ssr: false },
        // vue-quill-editor
        { src: "~plugins/vue-quill-editor.js", ssr: false },

    ],
    /*
    ** Customize the progress bar color
    */
    loading: {color: '#3B8070'},
    /*
    ** Build configuration
    */
    build: {
        /*
        ** Run ESLint on save
        */
        extend(config, {isDev, isClient}) {
            if (isDev && isClient) {
                config.module.rules.push({
                    enforce: 'pre',
                    test: /\.(js|vue)$/,
                    loader: 'eslint-loader',
                    exclude: /(node_modules)/
                })
            }
        }
    },
};

