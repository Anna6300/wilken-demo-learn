import { Selector } from 'testcafe';
import path from 'path';
import { pathToFileURL } from 'url';

// PORTAL_URL lets the same suite run against a local build, the cloud staging tenant or an on-prem test system.
const PORTAL_URL = process.env.PORTAL_URL
    || pathToFileURL(path.resolve('../src/main/resources/portal/abschlagsrechner.html')).href;

const byTestId = id => Selector(`[data-testid="${id}"]`);

fixture('Kundenportal – Abschlagsrechner')
    .page(PORTAL_URL);

test.meta({ requirement: 'BILL-102', tag: 'smoke' })(
    'Öko-Tarif: Abschlag für 2.000 kWh', async t => {
        // net 180 + 2000 * 0.35 = 880.00, gross 1047.20, / 12 = 87.27 -> 88 €
        await t
            .click(byTestId('tariff'))
            .click(byTestId('tariff').find('option').withAttribute('value', 'oeko'))
            .typeText(byTestId('consumption'), '2000', { replace: true })
            .click(byTestId('calculate'))
            .expect(byTestId('result').innerText).eql('Monatlicher Abschlag: 88 €');
    });

test.meta({ requirement: 'BILL-103' })(
    'Leeres Feld zeigt Fehlermeldung', async t => {
        await t
            .selectText(byTestId('consumption'))
            .pressKey('delete')
            .click(byTestId('calculate'))
            .expect(byTestId('error').innerText).eql('Bitte einen gültigen Verbrauch eingeben.')
            .expect(byTestId('result').innerText).eql('');
    });
