
const baseUrl = 'http://localhost:8080/api';

async function test() {
    try {
        console.log('Logging in...');
        let response = await fetch(`${baseUrl}/auth/signin`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: 'testuser@example.com',
                password: 'Password123!'
            })
        });

        if (!response.ok) {
            console.log('Login failed, trying signup...');
            response = await fetch(`${baseUrl}/auth/signup`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    email: 'testuser@example.com',
                    password: 'Password123!'
                })
            });
            console.log('Signup Status:', response.status);
            
            console.log('Retrying login...');
            response = await fetch(`${baseUrl}/auth/signin`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    email: 'testuser@example.com',
                    password: 'Password123!'
                })
            });
        }

        const loginData = await response.json();
        const token = loginData.token;
        console.log('Login successful, token received.');

        const headers = { 
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };

        console.log('Testing GET /api/incomes...');
        response = await fetch(`${baseUrl}/incomes`, { headers });
        console.log('GET /api/incomes Status:', response.status);
        if (response.ok) {
            const data = await response.json();
            console.log('Incomes count:', data.length);
        }

        console.log('Testing GET /api/tr-exp...');
        response = await fetch(`${baseUrl}/tr-exp`, { headers });
        console.log('GET /api/tr-exp Status:', response.status);
        const text = await response.text();
        console.log('Response body:', text);

        console.log('Testing GET /api/dashboard/stats...');
        response = await fetch(`${baseUrl}/dashboard/stats`, { headers });
        console.log('GET /api/dashboard/stats Status:', response.status);
        if (response.ok) {
            const data = await response.json();
            console.log('Stats:', JSON.stringify(data, null, 2));
        }

    } catch (err) {
        console.error('Test failed:');
        console.error(err.message);
    }
}

test();
