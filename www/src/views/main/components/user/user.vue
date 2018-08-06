<template>
    <div class="user-avator-dropdown">
        <Dropdown @on-click="handleClick">
            <Avatar :src="userAvator" />
            <Icon :size="18" type="md-arrow-dropdown"></Icon>
            <DropdownMenu slot="list">
                <DropdownItem name="logout">退出登录</DropdownItem>
            </DropdownMenu>
        </Dropdown>
    </div>
</template>

<script>
import { mapActions } from 'vuex';
export default {
    name: 'User',
    props: {
        userAvator: {
            type: String,
            default: ''
        }
    },
    methods: {
        ...mapActions([
            'handleLogOut'
        ]),
        async handleClick (name) {
            switch (name) {
                case 'logout':
                    await this.handleLogOut();
                    this.$router.push({
                        name: 'login'
                    });
                    break;
            }
        }
    }
};
</script>
<style lang="less">
.user {
    &-avator-dropdown {
        cursor: pointer;
        display: inline-block;
        // height: 64px;
        vertical-align: middle;
        // line-height: 64px;
    }
}
</style>
