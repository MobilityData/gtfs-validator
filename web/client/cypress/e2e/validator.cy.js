/// <reference types="cypress" />

const url = 'https://developers.google.com/static/transit/gtfs/examples/sample-feed.zip';
const jobId = '8f6be6fb-1fee-41f8-b401-b2b4b552e177-sample';

context('GTFS Validator - Core Workflow', () => {
  let numReportCalls = 0;

  beforeEach(() => {
    cy.visit('/')
  });

  it('Validate GTFS .zip file', () => {
    // Setup intercept aliases
    cy.intercept(
        'POST',
        `${Cypress.env("PUBLIC_CLIENT_API_ROOT")}/create-job`,
        (req) => {
          req.reply({
            statusCode: 200,
            statusMessage: "OK",
            headers: {
              "access-control-allow-origin": '*',
              "Access-Control-Allow-Credentials": "true",
            },
            body: {
              jobId: jobId
            }
          });
        }
      )
      .as('createJob');


    cy.intercept('HEAD',
      `${Cypress.env("PUBLIC_CLIENT_REPORTS_ROOT")}/*/report.html`,
      (req) => {
        if (numReportCalls > 0) {
          // return 200 code
          req.alias = 'awaitReport200'
          req.reply({
            statusCode: 200,
            headers: {
              "access-control-allow-origin": '*',
              "Access-Control-Allow-Credentials": "true",              
              "x-number-of-calls": `${numReportCalls}`
            }
          });
        } else {
          // return 404 code
          req.alias = 'awaitReport404'
          req.reply({
            statusCode: 404,
            headers: {
              "access-control-allow-origin": '*',
              "Access-Control-Allow-Credentials": "true",              
              "x-number-of-calls": `${numReportCalls}`
            }
          });
        }
        numReportCalls++;
      });

    // Enter URL to .zip file
    cy.get('input#url')
      .type(url);

    // Choose Region - US
    cy.get('select#region')
      .select(1);

    // Submit
    cy.get('button[type=submit]')
      .click();

    // Wait for 404
    cy.wait('@awaitReport404').its('response.statusCode').should('equal', 404);
    // Wait for 200
    cy.wait('@awaitReport200').its('response.statusCode').should('equal', 200);
  
   // Confirm "report ready"
   cy.get('dialog')
     .should('be.visible')
     .within(() => {
       cy.get('a.btn:contains("Open Report")').should('be.visible');
     });
  });
});
