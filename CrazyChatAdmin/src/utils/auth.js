import Cookie from "js-cookie";

const idKey = "userId";
const nameKey = "userName";
const avatarKey = "userAvatar";
const tokenKey = "userToken";

export function getUser() {
    return {
        "id": Cookie.get(idKey),
        "name": Cookie.get(nameKey),
        "avatar": Cookie.get(avatarKey),
        "token": Cookie.get(tokenKey),
    };
}

export function setUser(id, name, avatar, token) {
    Cookie.set(idKey, id);
    Cookie.set(nameKey, name);
    Cookie.set(avatarKey, avatar);
    Cookie.set(tokenKey, token);
}

export function removeUser() {
    Cookie.remove(idKey);
    Cookie.remove(nameKey);
    Cookie.remove(avatarKey);
    Cookie.remove(tokenKey);
}



