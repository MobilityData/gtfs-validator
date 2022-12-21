<script>
  /** @type {boolean|null} */
  export let disabled = null;
</script>

<div class="btn-content {disabled ? 'opacity-50' : ''}">
  <slot />
</div>
