import axios from 'axios';

jest.createMockFromModule('axios')

export default {
    get: jest.fn(() => Promise.resolve({ data: {} }))
};
