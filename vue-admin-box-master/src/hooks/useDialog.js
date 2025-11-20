import { ref } from 'vue'

export default function useDialog() {
  const visible = ref(false)

  function open() {
    visible.value = true
  }

  function close() {
    visible.value = false
  }


  return {
    visible,
    close,
    open,
  }
}
