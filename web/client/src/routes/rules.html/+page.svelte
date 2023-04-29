<script>
  import { dev } from '$app/environment';
  import { marked } from 'marked';
  import { page } from '$app/stores';
  import _ from 'lodash';

  const rules = $page.data.rules;

  $: categories = _.groupBy(rules, 'severityLevel');

  /** @param {string} filename */
  function getSpecRef(filename) {
    return `http://gtfs.org/reference/static#${filename.replace('.', '')}`;
  }

  /** @param {string} filename */
  function getBestPracticeRef(filename) {
    return `http://gtfs.org/best-practices/#${filename.replace('.', '')}`;
  }
</script>

<div class="container">
  <div class="flex flex-col md:flex-row md:gap-4 items-baseline">
    <a href="/" class="text-black/50 mr-auto">&larr; Back to validator</a>
    <!-- TODO will there still be a hard copy of this anywhere? -->
    <!-- <a
      href="https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md"
      class="text-black/50"
    >
      View this document on GitHub
    </a> -->
  </div>

  <div class="markdown mt-8 mb-16">
    {#if $page.data.msgHeading}
      <h1 class="h1">{$page.data.msgHeading}</h1>
    {:else}
      <h1 class="h1">Validator Rules</h1>
    {/if}

    {#if $page.data.msgBody}
      <p>
        {$page.data.msgBody}
      </p>
    {:else}
      <ul class="list-disc">
        {#each Object.entries(categories) as [category]}
          <li><a href="#{category}-table">Table of {category}s</a></li>
        {/each}
        <li><a href="#more-details">More details</a></li>
      </ul>

      {#each Object.entries(categories) as [category, rules]}
        <div id="{category}-table" class="mt-8">
          <h2 class="h2">
            Table of {category}s
            <a
              class="text-xl"
              href="#{category}-table"
              title="Link to this section"
            >
              <i class="fa-solid fa-hashtag text-black/30" />
            </a>
          </h2>

          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr>
                  <th>Notice code</th>
                  <th>Description</th>
                </tr>
              </thead>
              <tbody>
                {#each rules as rule}
                  <tr>
                    <td class="break-all md:break-normal">
                      <a href="#{rule.code}-rule">
                        <code>{rule.code}</code>
                      </a>
                    </td>
                    <td>{@html marked.parse(rule.description ?? '\u2014')}</td>
                  </tr>
                {/each}
              </tbody>
            </table>
          </div>
        </div>
      {/each}

      <h2 class="h2" id="more-details">More details</h2>

      {#each Object.entries(rules) as [code, rule]}
        <div id="{rule.code}-rule">
          <h3 class="h3">
            {code}
            <a
              class="text-base"
              href="#{rule.code}-rule"
              title="Link to this section"
            >
              <i class="fa-solid fa-hashtag text-black/30" />
            </a>
          </h3>

          {@html marked.parse(rule.description ?? '')}

          {#if rule.references}
            <h4 class="h4">References</h4>
            <ul>
              <!-- TODO: are there any other kinds of references? -->
              {#each rule.references?.fileReferences ?? [] as ref}
                <li>
                  <a href={getSpecRef(ref)} target="_blank" rel="noreferrer">
                    {ref} specification
                  </a>
                </li>
              {/each}
              {#each rule.references?.bestPracticesFileReferences ?? [] as ref}
                <li>
                  <a
                    href={getBestPracticeRef(ref)}
                    target="_blank"
                    rel="noreferrer"
                  >
                    {ref} best practices
                  </a>
                </li>
              {/each}
              {#each rule.references?.urlReferences ?? [] as ref}
                <li>
                  <a href={ref.url} target="_blank" rel="noreferrer">
                    {ref.label}
                  </a>
                </li>
              {/each}
            </ul>
          {/if}

          {#if rule.properties}
            <details>
              <summary>Fields</summary>
              <div class="overflow-x-auto">
                <table>
                  <thead>
                    <tr>
                      <th>Field name</th>
                      <th>Description</th>
                      <th>Type</th>
                    </tr>
                  </thead>
                  <tbody>
                    {#each Object.entries(rule.properties) as [name, property]}
                      <tr>
                        <td><code>{property.fieldName}</code></td>
                        <td>
                          {@html marked.parse(property.description ?? '\u2014')}
                        </td>
                        <td>{property.type ?? '\u2014'}</td>
                      </tr>
                    {/each}
                  </tbody>
                </table>
              </div>
            </details>
          {/if}
        </div>
      {/each}
    {/if}

    {#if dev}
      <div class="mt-16">
        <h2 class="h2">Dev only</h2>

        <details>
          <summary>Raw JSON</summary>
          <pre class="whitespace-pre-wrap">{JSON.stringify(
              rules,
              null,
              2
            )}</pre>
        </details>

        <details>
          <summary>Categories (grouped by severityLevel)</summary>
          <pre class="whitespace-pre-wrap">{JSON.stringify(
              categories,
              null,
              2
            )}</pre>
        </details>
      </div>
    {/if}
  </div>
</div>
