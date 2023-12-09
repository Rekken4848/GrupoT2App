function getCustomIcon(type) {
    const color = getColor(type);
    return L.divIcon({
        className: 'custom-icon',
        iconSize: [20, 20],
        iconAnchor: [10, 10],
        popupAnchor: [0, -10],
        html: `<div style="background-color: ${color}; width: 20px; height: 20px; border-radius: 50%;"></div>`
    });
}

function addToLegend(type) {
    const legendContent = document.getElementById('zonaMapa');
    const entry = document.createElement('div');
    entry.innerHTML = `<span class="legend-marker" style="background-color: ${getColor(type)};"></span>${type}`;
    legendContent.appendChild(entry);
}

function getColor(type) {
    // Asigna colores diferentes según el tipo de medición
    return type === 1 ? 'red' : type === 2 ? 'blue' : type === 3 ? 'yellow' : type === 4 ? 'pink' : 'green';
}

function generarYGenerarMapa() {
    const mymap = L.map('zonaMapa').setView([51.505, -0.09], 13);

    // Añade un mosaico de OpenStreetMap al mapa
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(mymap);

    mostrarLeyendaYMarcadores(mymap);

    // Añade un marcador al mapa
    L.marker([51.505, -0.09]).addTo(mymap)
        .bindPopup('¡Hola, mundo!').openPopup();
}

function mostrarLeyendaYMarcadores(mymap) {
    //IP Benidorm: 192.168.1.21
    fetch('http://192.168.1.21:8080/todasMediciones', {
        method: "GET"
    }).then(function (respuesta) {

        if (respuesta.ok) {
            return respuesta.json()
        } else {
            console.log("hubo un fallo")
        }

    }).then(function (datos) {
        console.log("Los datos del mapa bien" + JSON.stringify(datos));
        const datosJSON = JSON.stringify(datos)
        // Crea un objeto para almacenar las capas
        const capas = {};

        // Crea el control de capas y añádelo al mapa
        const controlCapas = L.control.layers(null, capas).addTo(mymap);

        datos.forEach(markerData => {
            const tipo = `Tipo ${markerData.tipo_valor_id}`;
            if (!capas[tipo]) {
                capas[tipo] = L.layerGroup(); // Crea una capa de grupo para cada tipo de dato
                const cuadroColor = `<span style="display:inline-block; width: 12px; height: 12px; background-color: ${getColor(Number(tipo.split(' ')[1]))}; margin-right: 5px;"></span>`;
                controlCapas.addOverlay(capas[tipo], `${cuadroColor} ${tipo}`);
            }
            // Dividir la cadena 'lugar' en latitud y longitud
            const [latitud, longitud] = markerData.lugar.split(',').map(parseFloat);

            console.log("Lat: " + latitud + ", Lon: " + longitud);
            const marker = L.marker([latitud, longitud], { icon: getCustomIcon(markerData.tipo_valor_id) }).addTo(mymap);

            // Personaliza el marcador según el tipo de medición
            marker.bindPopup(`Tipo: ${markerData.tipo_valor_id}<br>Valor: ${markerData.valor}`);
            addToLegend(markerData.tipo_valor_id);

            // Añade el marcador a la capa correspondiente
            capas[tipo].addLayer(marker);
        });

        // Agrega cuadros de color junto a cada tipo en el control de capas
        /*for (const tipo in capas) {
            const cuadroColor = `<span style="display:inline-block; width: 12px; height: 12px; background-color: ${getColor(Number(tipo.split(' ')[1]))}; margin-right: 5px;"></span>`;
            controlCapas.addOverlay(capas[tipo], `${cuadroColor} ${tipo}`);
        }*/
    })
}

generarYGenerarMapa();