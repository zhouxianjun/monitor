<template>
    <div class="custom-bread-crumb">
        <Breadcrumb :style="{fontSize: `${fontSize}px`}">
            <BreadcrumbItem v-for="item in list" :to="item.to" :key="`bread-crumb-${item.name}`">
                <common-icon style="margin-right: 4px;" :type="item.icon || ''"/>
                {{ showTitle(item) }}
            </BreadcrumbItem>
        </Breadcrumb>
    </div>
</template>
<script>
import { showTitle } from '@/libs/util';
import CommonIcon from '@/components/common-icon';
export default {
    name: 'CustomBreadCrumb',
    components: { CommonIcon },
    props: {
        list: {
            type: Array,
            default: () => []
        },
        fontSize: {
            type: Number,
            default: 14
        },
        showIcon: {
            type: Boolean,
            default: false
        }
    },
    methods: {
        showTitle (item) {
            return showTitle(item);
        }
    }
};
</script>
<style lang="less">
.custom-bread-crumb {
    display: inline-block;
    vertical-align: top;
}
</style>
