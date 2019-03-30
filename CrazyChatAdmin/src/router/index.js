import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            redirect: '/dashboard'
        },
        {
            path: '/',
            component: resolve => require(['../components/common/Home.vue'], resolve),
            children:[
                {
                    path: '/dashboard',
                    component: resolve => require(['../components/page/Dashboard.vue'], resolve),
                    meta: { title: '系统首页' }
                },
                {
                    path: '/user_list',
                    component: resolve => require(['../components/page/UserList.vue'], resolve),
                    meta: { title: 'CrazyChat用户列表' }
                },
                {
                    path: '/group_list',
                    component: resolve => require(['../components/page/GroupList.vue'], resolve),
                    meta: { title: 'CrazyChat群聊列表' }
                },
                {
                    path: '/group_member/:group_id',
                    component: resolve => require(['../components/page/GroupMembers.vue'], resolve),
                    meta: { title: '群成员' }
                },
                {
                    path: '/chat_record',
                    component: resolve => require(['../components/page/ChatRecord.vue'], resolve),
                    meta: { title: '用户聊天记录' }
                },
                {
                    path: '/shend_notice',
                    component: resolve => require(['../components/page/SendNotice.vue'], resolve),
                    meta: { title: '发送公告' }
                },
                {
                    path: '/404',
                    component: resolve => require(['../components/page/404.vue'], resolve),
                    meta: { title: '404' }
                },
                {
                    path: '/403',
                    component: resolve => require(['../components/page/403.vue'], resolve),
                    meta: { title: '403' }
                }
            ]
        },
        {
            path: '/login',
            component: resolve => require(['../components/page/Login.vue'], resolve)
        },
        {
            path: '*',
            redirect: '/404'
        }
    ]
});
