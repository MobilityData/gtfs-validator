<script>
  import { marked } from 'marked';
  import { page } from '$app/stores';
  import { onMount } from 'svelte';

  /** @type {string} */
  let massagedMarkdown = '';

  $: {
    const md = $page.data.rulesMd;

    // edit markdown to help with parsing
    // marked does not like some of the formatting in RULES.md
    massagedMarkdown = md
      .replace(/<a name=".*"\/>/gm, '') // remove redundant anchors
      .replace(/\t/gm, '    ') // spaceify tabs
      .replace(/(\S)$\n<details>/gm, '$1\n\n<details>'); // ensure blank lines before <details>

    // use github as a base url for relative links
    massagedMarkdown = massagedMarkdown.replace(
      /\[(.*)\]\(\/(.*)\)/gm,
      '[$1](https://github.com/MobilityData/gtfs-validator/blob/master/$2)'
    );
  }

  onMount(() => {
    // wrap tables in scrollable divs
    const tableWrapper = document.createElement('div');
    tableWrapper.classList.add('overflow-x-scroll');
    document.querySelectorAll('table').forEach((table) => {
      const wrapper = tableWrapper.cloneNode(false);
      table.parentNode?.insertBefore(wrapper, table);
      wrapper.appendChild(table);
    });
  });
</script>

<div class="container">
  <div class="flex flex-col md:flex-row md:gap-4 items-baseline">
    <a href="/" class="text-black/50 mr-auto">&larr; Back to validator</a>
    <a
      href="https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md"
      class="text-black/50"
    >
      View this document on GitHub
    </a>
  </div>

  <div class="markdown mt-8 mb-16">
    <!--
      note: marked does not sanitize output
      @html is unsafe when loading external content
    -->
    {@html marked.parse(massagedMarkdown)}
  </div>
</div>
