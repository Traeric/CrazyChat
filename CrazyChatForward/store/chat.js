export const state = () => ({
    sendType: 0,
});


export const mutations = {
    setSendType(state, type) {
        state.sendType = type;
    },
};


export const actions = {
    setSendType({ commit }, type) {
        commit("setSendType", type);
    },
};

