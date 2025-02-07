/// <reference types="cypress" />
const url =
  'https://developers.google.com/static/transit/gtfs/examples/sample-feed.zip';
const jobId = '8f6be6fb-1fee-41f8-b401-b2b4b552e177-sample';
const execution_result_json = "execution_result.json"

context('GTFS Validator - Confirm error messaging', () => {
  it('Confirm error "Error authorizing upload"', () => {
    // Setup intercept aliases
    cy.intercept(
      'POST',
      `${Cypress.env('PUBLIC_CLIENT_API_ROOT')}/create-job`,
      { forceNetworkError: true }
    ).as('createJob');

    cy.visit('/');

    // Upload .zip file
    cy.get('input#file').selectFile('cypress/fixtures/sample-feed.zip', {
      force: true,
    });

    // Submit
    cy.get('button[type=submit]').click();

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
      `https://storage.googleapis.com/stg-validator-user-uploads/${jobId}/gtfs-job.zip?X-Goog-Algorithm=GOOG4-RSA-SHA256`,
      { forceNetworkError: true }
    ).as('putFile');

    cy.intercept(
      'POST',
      `${Cypress.env('PUBLIC_CLIENT_API_ROOT')}/create-job`,
      (req) => {
        req.reply({
          statusCode: 200,
          statusMessage: 'OK',
          headers: {
            'access-control-allow-origin': '*',
            'Access-Control-Allow-Credentials': 'true',
          },
          body: {
            jobId: jobId,
            url: `https://storage.googleapis.com/stg-validator-user-uploads/${jobId}/gtfs-job.zip?X-Goog-Algorithm=GOOG4-RSA-SHA256`,
          },
        });
      }
    ).as('createJob');

    cy.visit('/');

    // Upload .zip file
    cy.get('input#file').selectFile('cypress/fixtures/sample-feed.zip', {
      force: true,
    });

    // Submit
    cy.get('button[type=submit]').click();

    cy.wait('@createJob').its('response.statusCode').should('equal', 200);

    // Wait for response error
    cy.wait('@putFile', { timeout: 10000 });

    // Confirm "error uploading file"
    cy.get('.alert')
      .should('be.visible')
      .and('contain.text', 'Error uploading file');
  });

  it('Confirm error "Error processing report" when network fails with the execution_results file', () => {
    // Setup intercept aliases
    cy.intercept(
      'POST',
      `${Cypress.env('PUBLIC_CLIENT_API_ROOT')}/create-job`,
      (req) => {
        req.reply({
          statusCode: 200,
          statusMessage: 'OK',
          headers: {
            'access-control-allow-origin': '*',
            'Access-Control-Allow-Credentials': 'true',
          },
          body: {
            jobId: jobId,
            url: `https://storage.googleapis.com/stg-validator-user-uploads/${jobId}/gtfs-job.zip?X-Goog-Algorithm=GOOG4-RSA-SHA256`,
          },
        });
      }
    ).as('createJob');

    cy.intercept(
      'HEAD',
      `${Cypress.env("PUBLIC_CLIENT_REPORTS_ROOT")}/*/${execution_result_json}`,
      { forceNetworkError: true }
      )
      .as('awaitExecutionResults');

    cy.visit('/');

    // Enter URL to .zip file
    cy.get('input#url').type(url);

    // Submit
    cy.get('button[type=submit]').click();

    cy.wait('@createJob').its('response.statusCode').should('equal', 200);

    // Wait for responses
    cy.wait('@awaitExecutionResults');

    // Confirm "Error processing report"
    cy.get('.alert')
      .should('be.visible')
      .and('contain.text', 'Error processing report');
  });

  it('Confirm error "Error processing report" when network fails with the html report', () => {
    // Setup intercept aliases
    cy.intercept(
      'POST',
      `${Cypress.env('PUBLIC_CLIENT_API_ROOT')}/create-job`,
      (req) => {
        req.reply({
          statusCode: 200,
          statusMessage: 'OK',
          headers: {
            'access-control-allow-origin': '*',
            'Access-Control-Allow-Credentials': 'true',
          },
          body: {
            jobId: jobId,
            url: `https://storage.googleapis.com/stg-validator-user-uploads/${jobId}/gtfs-job.zip?X-Goog-Algorithm=GOOG4-RSA-SHA256`,
          },
        });
      }
    ).as('createJob');

    cy.intercept('HEAD', `${Cypress.env("PUBLIC_CLIENT_REPORTS_ROOT")}/*/${execution_result_json}`, {
      statusCode: 200
    })
    .as('awaitExecutionResultsHead')

    cy.intercept('GET', `${Cypress.env("PUBLIC_CLIENT_REPORTS_ROOT")}/*/${execution_result_json}`, {
      statusCode: 200,
      body: {
        status: 'success',
      },
    })
    .as('awaitExecutionResults')

    cy.intercept(
      'HEAD',
      `${Cypress.env("PUBLIC_CLIENT_REPORTS_ROOT")}/*/report.html`,
      { forceNetworkError: true }
      )
      .as('awaitReportFailing');

    cy.visit('/');

    // Enter URL to .zip file
    cy.get('input#url').type(url);

    // Submit
    cy.get('button[type=submit]').click();

    cy.wait('@createJob').its('response.statusCode').should('equal', 200);

    // Wait for responses
    cy.wait('@awaitExecutionResultsHead');
    cy.wait('@awaitExecutionResults');

    cy.wait('@awaitReportFailing');

    // Confirm "Error processing report"
    cy.get('.alert')
      .should('be.visible')
      .and('contain.text', 'Error processing report');
  });

  it('Confirm error "Error processing report" when the execution result status is error', () => {
    // Setup intercept aliases
    cy.intercept(
      'POST',
      `${Cypress.env('PUBLIC_CLIENT_API_ROOT')}/create-job`,
      (req) => {
        req.reply({
          statusCode: 200,
          statusMessage: 'OK',
          headers: {
            'access-control-allow-origin': '*',
            'Access-Control-Allow-Credentials': 'true',
          },
          body: {
            jobId: jobId,
            url: `https://storage.googleapis.com/stg-validator-user-uploads/${jobId}/gtfs-job.zip?X-Goog-Algorithm=GOOG4-RSA-SHA256`,
          },
        });
      }
    ).as('createJob');

    cy.intercept('HEAD', `${Cypress.env("PUBLIC_CLIENT_REPORTS_ROOT")}/*/${execution_result_json}`, {
      statusCode: 200
    })
    .as('awaitExecutionResultsHead')

    cy.intercept('GET', `${Cypress.env("PUBLIC_CLIENT_REPORTS_ROOT")}/*/${execution_result_json}`, {
      statusCode: 200,
      body: {
        status: 'error',
      },
    })
    .as('awaitExecutionResults')

    cy.visit('/');

    // Enter URL to .zip file
    cy.get('input#url').type(url);

    // Submit
    cy.get('button[type=submit]').click();

    cy.wait('@createJob').its('response.statusCode').should('equal', 200);

    // Wait for responses
    cy.wait('@awaitExecutionResultsHead');
    cy.wait('@awaitExecutionResults');

    // Confirm "Error processing report"
    cy.get('.alert')
      .should('be.visible')
      .and('contain.text', 'Error processing report');
  });

  it('Confirm error "HTTP Error: 404"', () => {
    // Setup intercept aliases
    cy.intercept('rules.json', (req) => {
      req.reply({
        statusCode: 404,
        headers: {
          'access-control-allow-origin': '*',
          'Access-Control-Allow-Credentials': 'true',
        },
      });
    }).as('getDocMarkdown');

    cy.intercept('summary-metadata.json', (req) => {
      req.reply({
        statusCode: 404,
        headers: {
          'access-control-allow-origin': '*',
          'Access-Control-Allow-Credentials': 'true',
        },
      });
    }).as('getSummaryMetadata');

    cy.visit('/');

    // Click "See Documentation" button
    cy.get('a:contains("See Documentation")').click();

    cy.location('pathname', { timeout: 60000 }).should(
      'include',
      '/rules.html'
    );

    // Confirm error content
    cy.get('.container .markdown')
      .should('be.visible')
      .within((div) => {
        cy.get('h1').should('contain.text', 'HTTP Error: 404');
        cy.get('p').should(
          'contain.text',
          'There was a problem loading the rules file.'
        );
      });
  });
});
