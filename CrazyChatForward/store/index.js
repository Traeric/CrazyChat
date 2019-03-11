export const state = () => ({
    authenticated: null,
});


export const mutations = {
    setAuthenticated(state, type) {
        state.authenticated = type || null;
    },
};


export const actions = {
    setAuthenticated({commit}, type) {
        commit("setAuthenticated", type);
    },
};
