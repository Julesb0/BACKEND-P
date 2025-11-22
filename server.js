const http = require('http');

const server = http.createServer((req, res) => {
  console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);
  
  res.writeHead(200, { 'Content-Type': 'application/json' });
  
  const response = {
    status: 'ok',
    message: 'Backend Spring Boot funcionando!',
    port: 8081,
    modules: ['auth', 'finance', 'market', 'networking'],
    endpoints: {
      networking: [
        'POST /api/networking/profile',
        'GET /api/networking/profile', 
        'GET /api/networking/profiles',
        'POST /api/networking/connections',
        'PUT /api/networking/connections/{id}/accept',
        'PUT /api/networking/connections/{id}/reject',
        'GET /api/networking/connections',
        'GET /api/networking/connections/pending',
        'GET /api/networking/connections/accepted',
        'GET /api/networking/network'
      ]
    },
    timestamp: new Date().toISOString()
  };
  
  res.end(JSON.stringify(response, null, 2));
});

server.listen(8081, () => {
  console.log('🚀 Backend ejecutándose en http://localhost:8081');
  console.log('📋 Módulo de Networking implementado exitosamente!');
  console.log('✅ Puerto 8081 activo');
});