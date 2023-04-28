<script>
  import { backOut, backIn, circInOut, elasticOut } from 'svelte/easing';
  import { fade, fly, scale, slide } from 'svelte/transition';

  import Button from './Button.svelte';
  import CopyButton from './CopyButton.svelte';

  /** @type {?HTMLDialogElement} */
  export let dialog = null;

  /** @type {string} */
  export let status;

  /** @type {string} */
  export let reportUrl;

  $: isReady = status === 'ready';
  $: reportUrlJson = reportUrl.replace(/.html$/, '.json');

  /** @type {Object.<string, string>} */
  const statusMessages = {
    error: 'Report not found',
    authorizing: 'Preparing upload\u2026',
    uploading: 'Uploading file\u2026',
    processing: 'Validating GTFS data\u2026',
    ready: 'Your report is\u00a0ready!',
  };
</script>

<dialog
  bind:this={dialog}
  on:close
  class="backdrop:bg-mobi-light-blue/20 backdrop:backdrop-blur-sm
  rounded-lg shadow-2xl shadow-indigo-800/50
  overflow-hidden
  p-8 w-80
  text-center
  transition-all
"
>
  {#if ['error', 'ready'].includes(status)}
    <form method="dialog" class="flex justify-end -mt-7 -mr-7">
      <span in:scale|local>
        <Button type="submit" class="!px-4" variant="link">
          <i class="fa-solid fa-times" />
          <div class="sr-only">Dismiss</div>
        </Button>
      </span>
    </form>
  {/if}

  <div class="grid">
    {#key status}
      <div
        style="grid-area: 1/1/1/1"
        in:fly|local={{ easing: circInOut, x: 128 }}
        out:fly|local={{ easing: circInOut, x: -128 }}
      >
        {#if status == 'authorizing'}
          <i class="fa-solid fa-2x fa-file-lines fa-flip text-mobi-purple" />
        {:else if status == 'uploading'}
          <i
            class="fa-solid fa-2x fa-cloud-arrow-up fa-beat text-mobi-purple"
          />
        {:else if status == 'processing'}
          <i
            class="fa-solid fa-2x fa-gear fa-spin text-mobi-purple-safe"
            style="--fa-animation-duration: 1s"
          />
        {:else if status == 'ready'}
          {#key status}
            <i
              in:scale|local={{ delay: 400, duration: 800, easing: elasticOut }}
              class="fa-solid fa-2x fa-check-circle text-mobi-light-blue"
            />
          {/key}
        {:else if status == 'error'}
          <i class="fa-regular fa-2x fa-face-frown text-mobi-purple" />
        {/if}
      </div>
    {/key}
  </div>

  {#key status}
    <div
      class="status-message leading-snug mt-4"
      in:fade|local={{ duration: 250 }}
    >
      {statusMessages[status]}
    </div>
  {/key}

  {#if status == 'authorizing' || status == 'processing'}
    <div in:slide|local={{ delay: 5000 }} out:slide|local class="opacity-40">
      This may take a&nbsp;moment
    </div>
    <div in:slide|local={{ delay: 20000 }} out:slide|local class="opacity-40">
      Hang tight!
    </div>
  {/if}

  {#if status == 'error'}
    <div
      in:slide|local={{ easing: backOut }}
      out:slide|local={{ easing: backIn }}
    >
      <div class="text-black/50">Has it been more than 30&nbsp;days?</div>

      <form method="dialog" class="flex flex-col items-stretch mt-8">
        <Button type="submit">Start Over</Button>
      </form>
    </div>
  {/if}

  {#if isReady && reportUrl}
    <div
      in:slide|local={{ easing: backOut }}
      out:slide|local={{ easing: backIn }}
      class="mt-8 flex flex-col gap-2"
    >
      <Button href={reportUrl} target="_blank">
        Open Report
        <i class="fa-solid fa-xs fa-arrow-up-right-from-square" />
      </Button>

      <hr class="my-4 " />

      <p class="mb-4 text-sm text-black/50">
        You may copy and share the links below. Your report will be available
        for at least 30&nbsp;days.
      </p>

      <div class="flex items-baseline gap-2 text-sm">
        <CopyButton
          href={reportUrl}
          class="flex-1"
          size="sm"
          content={reportUrl}>HTML</CopyButton
        >
        <CopyButton
          href={reportUrlJson}
          class="flex-1"
          size="sm"
          content={reportUrlJson}>JSON</CopyButton
        >
      </div>
    </div>
  {/if}
</dialog>
