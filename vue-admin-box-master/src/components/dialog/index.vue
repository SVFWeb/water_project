<template>
    <div>
        <el-dialog :model-value="visible" :title="title" :width="width" :before-close="close">
            <slot name="content"></slot>
            <div>
                <el-button type="primary" @click="confirm">确认</el-button>
                <el-button @click="close">取消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
const prop = defineProps({
    visible: Boolean,
    title: String,
    width: {
        type: String,
        default: '600px'
    }
})
const emit = defineEmits(['update:visible', 'confirm', 'close'])

function confirm() {
    emit('confirm')
}

function close() {
    emit('update:visible', false)
    emit('close')
}


</script>

<style lang="scss" scoped></style>