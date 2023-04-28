/// <reference types="cypress" />

context('GTFS Validator - Confirm error messaging', () => {
  beforeEach(() => {
    cy.visit('https://gtfs-validator.mobilitydata.org/')
  });

  it('Confirm error "Error authorizing upload"', () => {
    // Setup intercept aliases
    cy.intercept(
      'POST',
      'https://*/create-job',
      { forceNetworkError: true }
    )
    .as('createJob');

    // Upload .zip file
    cy.get('input#file')
      .selectFile('cypress/fixtures/sample-feed.zip', { force: true });

    // Submit
    cy.get('button[type=submit]')
      .click();

    // Wait for response error
    cy.wait('@createJob');

    // Confirm "error uploading file"
    cy.get('.alert')
      .should('be.visible')
      .and('contain.text', 'Error authorizing upload');
  });

  it('Confirm error "Error uploading file"', () => {
    // Setup intercept aliases
    cy.intercept(
      'PUT',
      'https://storage.googleapis.com/gtfs-validator-user-uploads/*/gtfs-job.zip?*',
      { forceNetworkError: true }
    )
    .as('putFile');

    // Upload .zip file
    cy.get('input#file')
      .selectFile('cypress/fixtures/sample-feed.zip', { force: true });

    // Submit
    cy.get('button[type=submit]').click();

    // Wait for response error
    cy.wait('@putFile', { timeout: 10000 });

    // Confirm "error uploading file"
    cy.get('.alert')
      .should('be.visible')
      .and('contain.text', 'Error uploading file');
  });

  it('Confirm error "Error processing report"', () => {
    const url = 'https://developers.google.com/static/transit/gtfs/examples/sample-feed.zip';
    const jobId = '8f6be6fb-1fee-41f8-b401-b2b4b552e177';

    // Setup intercept aliases
    cy.intercept(
        'POST',
        'https://*/create-job',
        (req) => {
          req.reply({
            statusCode: 200,
            statusMessage: "OK",
            body: {
              jobId: jobId
            }
          });
        }
      )
      .as('createJob');

    cy.intercept(
      'HEAD',
      'https://gtfs-validator-results.mobilitydata.org/*/report.html',
      { forceNetworkError: true }
      )
      .as('awaitJob');

    // Enter URL to .zip file
    cy.get('input#url').type(url);

    // Submit
    cy.get('button[type=submit]').click();

    // Wait for responses
    cy.wait(['@createJob', '@awaitJob']);

    // Confirm "Error processing report"
    cy.get('.alert')
      .should('be.visible')
      .and('contain.text', 'Error processing report');
  });

  it('Confirm error "HTTP Error: 404"', () => {
    // Setup intercept aliases
    cy.intercept(
        'https://gtfs-validator.mobilitydata.org/RULES.md',
        (req) => {
          req.reply({
            statusCode: 404
          });
        }
      )
      .as('getDocMarkdown');

    // Click "See Documentation" button
    cy.get('a:contains("See Documentation")').click();

    // Wait for response
    cy.wait('@getDocMarkdown');

    // Confirm error content
    cy.get('.container .markdown')
      .should('be.visible')
      .within((div) => {
        cy.get('h1').should('contain.text', 'HTTP Error: 404');
        cy.get('p').should('contain.text', 'There was a problem loading the rules file. You can try accessing it directly at');
        cy.get('a')
          .should('contain.text', 'https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md')
          .and('have.attr', 'href', 'https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md');
      })
  });
});
