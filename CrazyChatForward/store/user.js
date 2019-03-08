export const state = () => ({
    personnalInfoDialog: false,
    userInfo: {},
});


export const mutations = {
    setpersonnalInfoDialog(state, flag) {
        state.personnalInfoDialog = flag;
    },
    setUserInfo(state, info) {
        state.userInfo = info;
    },
};


export const actions = {
    setpersonnalInfoDialog({commit}, flag) {
        commit("setpersonnalInfoDialog", flag);
    },
    setUserInfo({commit}, info) {
        commit("setUserInfo", info);
    },
};

