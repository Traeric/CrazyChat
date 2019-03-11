export default function (context) {
    console.log(context);
    if (!context.store.state.authenticated) {
        // 未登录，跳转到登录页面
        return context.redirect("/login");
    }
}

