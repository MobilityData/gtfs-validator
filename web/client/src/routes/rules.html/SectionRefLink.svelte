<script>
  /**
   * @typedef {Object} SectionReference
   * @property {string} label
   * @property {string} url
   */

   /** @type {Object.<string, SectionReference>} */
   const sectionReferences = {
    'dataset-files': {
      label: 'Dataset Files',
      url: 'https://gtfs.org/schedule/reference/#dataset-files',
    },
    'dataset-publishing-general-practices': {
      label: 'Dataset Publishing & General Practices',
      url: 'https://gtfs.org/schedule/best-practices/#dataset-publishing-general-practices',
    },
    'field-definitions': {
      label: 'Field Definitions',
      url: 'https://gtfs.org/schedule/reference/#field-definitions',
    },
    'field-types': {
      label: 'Field Types',
      url: 'https://gtfs.org/schedule/reference/#field-types',
    },
    'file-requirements': {
      label: 'File Requirements',
      url: 'https://gtfs.org/schedule/reference/#file-requirements',
    },
    'term-definitions': {
      label: 'Term Definitions',
      url: 'https://gtfs.org/schedule/reference/#term-definitions',
    },
  };

  /** @type {string} */
  export let ref;

  /** @type {SectionReference}*/
  $: sectionReference = sectionReferences[ref] ?? {
    label: `${ref} section`,
    url: `https://gtfs.org/schedule/reference/#${ref.replace('.', '')}`,
  }
</script>

<a href={sectionReference.url} target="_blank" rel="noreferrer">
  {sectionReference.label}
</a>
