<template>
    
</template>

<script>
    export default {
        name: "app",
        async mounted() {
            this.doQuery();
        },
        methods: {
            async doQuery() {
                let list = await this.fetch('/api/app/list');
                console.log(list);
            }
        }
    }
</script>

<style scoped>

</style>