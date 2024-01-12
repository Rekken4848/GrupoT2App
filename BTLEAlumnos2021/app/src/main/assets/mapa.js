// .......................................................
// .......................................................
// .......................MAPA............................
// .......................................................
// .......................................................

// .......................................................
// .......................................................
// ......................GENERAL..........................
// .......................................................
// .......................................................

let mymap;
let currentPosition;

// .......................................................
// fechaInicio, fechaFin --> generarYGenerarMapa()
// .......................................................
function generarYGenerarMapa(fechaInicio, fechaFin, pagina) {
    if (mymap) {
        currentPosition = {
            lat: mymap.getCenter().lat,
            lng: mymap.getCenter().lng,
            zoom: mymap.getZoom()
        };
        mymap.remove();
    }

    if (currentPosition && currentPosition.lat !== undefined && currentPosition.lng !== undefined) {
        //console.log("Latitud: " + currentPosition.lat + " Longitud: " + currentPosition.lng + " Zoom: " + currentPosition.zoom);
        mymap = L.map('zonaMapa2').setView([currentPosition.lat, currentPosition.lng], currentPosition.zoom);
    } else {
        mymap = L.map('zonaMapa2').setView([38.972375, -0.177042], 13);
    }

    //mymap = L.map('zonaMapa').setView([51.505, -0.09], 13);
    //mymap = L.map('zonaMapa').setView([38.972375, -0.177042], 13);

    // Añade un mosaico de OpenStreetMap al mapa
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(mymap);

    // Add the air quality stations WMS layer-----------------------------
    L.tileLayer.wms('https://wms.mapama.gob.es/sig/EvaluacionAmbiental/CalidadAire/RedEstacionesCa/wms.aspx?', {
        layers: 'ESTACIONES_CA',
        format: 'image/png',
        transparent: true
    }).addTo(mymap);

    // Tipos de datos para tu ejemplo
    const tiposDeDatos = ['Alto', 'Medio', 'Bajo'];

    // Crea y agrega la leyenda al mapa
    const legend = createLegend(tiposDeDatos);
    legend.addTo(mymap);

    //filtrarPorFecha();
    /*const fechaInicio = '2023-10-14 16:32:40'
    const fechaFin = '2023-10-16 16:32:40'
    const [fechaHoyFuera, fechaAyerFuera] = getFechaHoy()
    console.log("Fecha hoy fuera:" + fechaHoyFuera + "Fecha ayer fuera:" + fechaAyerFuera)*/

    mostrarLeyendaYMarcadores(mymap, fechaInicio, fechaFin, pagina);

    // Añade un marcador al mapa
    /*L.marker([51.505, -0.09]).addTo(mymap)
        .bindPopup('¡Hola, mundo!').openPopup();*/
}

// .......................................................
// Mapa, fechaInicio, fechaFin --> mostrarLeyendaYMarcadores()
// .......................................................
function mostrarLeyendaYMarcadores(mymap, fechaInicio, fechaFin, pagina) {
    fetch('http://192.168.43.252:8080/medicionConTipoValorEntreFechasYDispositivo/2023-10-14 16:32:40/2023-10-16 16:32:40/PrueMapaDis', {
        method: "GET"
    }).then(function (respuesta) {

        if (respuesta.ok) {
            return respuesta.json()
        } else {
            console.log("hubo un fallo")
        }

    }).then(function (datos) {
        //console.log("Los datos del mapa bien" + JSON.stringify(datos));
        const datosJSON = JSON.stringify(datos)
        datosMediciones = datos
        // Crea un objeto para almacenar las capas
        const capas = {};

        // Crea el control de capas y añádelo al mapa
        const controlCapas = L.control.layers(null, capas).addTo(mymap);

        const alto = [];
        const medio = [];
        const bajo = [];

        datos.forEach(markerData => {
            const tipo = `${markerData.tipo_valor}`;

            // Clasifica la medición en la lista correspondiente
            switch (tipo) {
                case "O3":
                    if (markerData.valor > 240) {
                        alto.push(markerData);
                    } else if (markerData.valor > 180) {
                        medio.push(markerData);
                    } else {
                        bajo.push(markerData);
                    }
                    break;
                case "NO2":
                    if (markerData.valor > 200) {
                        alto.push(markerData);
                    } else if (markerData.valor > 100) {
                        medio.push(markerData);
                    } else {
                        bajo.push(markerData);
                    }
                    break;
                case "SO2":
                    if (markerData.valor > 350) {
                        alto.push(markerData);
                    } else if (markerData.valor > 125) {
                        medio.push(markerData);
                    } else {
                        bajo.push(markerData);
                    }
                    break;
                case "CO":
                    if (markerData.valor > 10) {
                        alto.push(markerData);
                    } else if (markerData.valor > 5) {
                        medio.push(markerData);
                    } else {
                        bajo.push(markerData);
                    }
                    break;
                case "C6H6":
                    if (markerData.valor > 5) {
                        alto.push(markerData);
                    } else if (markerData.valor > 2) {
                        medio.push(markerData);
                    } else {
                        bajo.push(markerData);
                    }
                    break;
            }
        });

        datos.forEach(markerData => {
            const tipo = `${markerData.tipo_valor}`;
            if (!capas[tipo]) {
                capas[tipo] = L.layerGroup(); // Crea una capa de grupo para cada tipo de dato
                //console.log("Número de tipo_valor:", Number(tipo.split(' ')[1]));
                //(COMENTADO POR THOMAS)const cuadroColor = `<span style="display:inline-block; width: 12px; height: 12px; background-color: ${getColor(markerData.tipo_valor)}; margin-right: 5px;"></span>`;
                controlCapas.addOverlay(capas[tipo], `${tipo}`);
                controlCapas.click = true;
            }
        });

        var puntosInterpolacion = []

        datos.forEach(markerData => {
            const [latitud, longitud] = markerData.lugar.split(',').map(parseFloat);
            //puntosInterpolacion.push([latitud, longitud, markerData.valor]) parseInt(d, 10);
            puntosInterpolacion.push([latitud, longitud, parseInt(markerData.valor, 10)])
        });

        console.log("Alto: " + JSON.stringify(alto));
        console.log("Medio: " + JSON.stringify(medio));
        console.log("Bajo: " + JSON.stringify(bajo));

        if(pagina==="Ciudadano"){
            mapaInterpolado(datos, capas, mymap);
        } else if(pagina==="Admin"){
            puntosMapaNormal(alto, medio, bajo, capas, mymap);
        }
        //Espacio estacion medida oficial -- INICIO
        obtenerEstacionMedidaOficial(mymap);
        //Espacio estacion medida oficial -- FIN
    })
}

function puntosMapaNormal(alto, medio, bajo, capas, mymap){
    alto.forEach(markerData => {
        const tipo = `${markerData.tipo_valor}`;

        // Dividir la cadena 'lugar' en latitud y longitud
        const [latitud, longitud] = markerData.lugar.split(',').map(parseFloat);

        //console.log("Lat: " + latitud + ", Lon: " + longitud);
        const marker = L.marker([latitud, longitud], { icon: getCustomIcon('Alto') }).addTo(mymap);
        marker.bindPopup(`Tipo: ${markerData.tipo_valor}<br>Valor: ${markerData.valor}`);
        addToLegend(markerData.tipo_valor);
        //marker.setIcon(getCustomIcon(markerData.tipo_valor, 'red'));

        marcadores.push(marker)

        // Añade el marcador a la capa correspondiente
        capas[tipo].addLayer(marker);
    });

    medio.forEach(markerData => {
        const tipo = `${markerData.tipo_valor}`;

        // Dividir la cadena 'lugar' en latitud y longitud
        const [latitud, longitud] = markerData.lugar.split(',').map(parseFloat);

        //console.log("Lat: " + latitud + ", Lon: " + longitud);
        const marker = L.marker([latitud, longitud], { icon: getCustomIcon('Medio') }).addTo(mymap);
        marker.bindPopup(`Tipo: ${markerData.tipo_valor}<br>Valor: ${markerData.valor}`);
        addToLegend(markerData.tipo_valor);
        //marker.setIcon(getCustomIcon(markerData.tipo_valor, 'yellow'));

        marcadores.push(marker)

        // Añade el marcador a la capa correspondiente
        capas[tipo].addLayer(marker);
    });

    bajo.forEach(markerData => {
        const tipo = `${markerData.tipo_valor}`;

        // Dividir la cadena 'lugar' en latitud y longitud
        const [latitud, longitud] = markerData.lugar.split(',').map(parseFloat);

        //console.log("Lat: " + latitud + ", Lon: " + longitud);
        const marker = L.marker([latitud, longitud], { icon: getCustomIcon('Bajo') }).addTo(mymap);
        marker.bindPopup(`Tipo: ${markerData.tipo_valor}<br>Valor: ${markerData.valor}`);
        addToLegend(markerData.tipo_valor);
        //marker.setIcon(getCustomIcon(markerData.tipo_valor, 'green'));

        marcadores.push(marker)

        // Añade el marcador a la capa correspondiente
        capas[tipo].addLayer(marker);
    });
}

function mapaInterpolado(datos, capas, mymap){
    const O3 = [];
    const NO = [];
    const SO2 = [];
    const CO = [];
    const C6H6 = [];

    datos.forEach(markerData => {
        const tipo = `${markerData.tipo_valor}`;

        // Clasifica la medición en la lista correspondiente
        if (markerData.tipo_valor === "O3") {
            O3.push(markerData);
        } else if (markerData.tipo_valor === "NO") {
            NO.push(markerData);
        } else if (markerData.tipo_valor === "SO2") {
            SO2.push(markerData);
        } else if (markerData.tipo_valor === "CO") {
            CO.push(markerData);
        } else if (markerData.tipo_valor === "C6H6") {
            C6H6.push(markerData);
        }
    });

    var puntosInterpolacionO3 = []

    O3.forEach(markerData => {
        const [latitud, longitud] = markerData.lugar.split(',').map(parseFloat);
        //puntosInterpolacion.push([latitud, longitud, markerData.valor]) parseInt(d, 10);
        puntosInterpolacionO3.push([latitud, longitud, parseInt(markerData.valor, 10)])
    });

    var puntosInterpolacionNO = []

    NO.forEach(markerData => {
        const [latitud, longitud] = markerData.lugar.split(',').map(parseFloat);
        //puntosInterpolacion.push([latitud, longitud, markerData.valor]) parseInt(d, 10);
        puntosInterpolacionNO.push([latitud, longitud, parseInt(markerData.valor, 10)])
    });

    var puntosInterpolacionSO2 = []

    SO2.forEach(markerData => {
        const [latitud, longitud] = markerData.lugar.split(',').map(parseFloat);
        //puntosInterpolacion.push([latitud, longitud, markerData.valor]) parseInt(d, 10);
        puntosInterpolacionSO2.push([latitud, longitud, parseInt(markerData.valor, 10)])
    });

    var puntosInterpolacionCO = []

    CO.forEach(markerData => {
        const [latitud, longitud] = markerData.lugar.split(',').map(parseFloat);
        //puntosInterpolacion.push([latitud, longitud, markerData.valor]) parseInt(d, 10);
        puntosInterpolacionCO.push([latitud, longitud, parseInt(markerData.valor, 10)])
    });

    var puntosInterpolacionC6H6 = []

    C6H6.forEach(markerData => {
        const [latitud, longitud] = markerData.lugar.split(',').map(parseFloat);
        //puntosInterpolacion.push([latitud, longitud, markerData.valor]) parseInt(d, 10);
        puntosInterpolacionC6H6.push([latitud, longitud, parseInt(markerData.valor, 10)])
    });

    if (puntosInterpolacionO3 != 0) {
        var idwO3 = L.idwLayer(puntosInterpolacionO3, {
            opacity: 0.3,
            maxZoom: 18,
            cellSize: 10,
            exp: 3,
            max: 270,
            gradient: { 0: 'green', 0.77: 'yellow', 1: 'red' }
        }).addTo(mymap);
        const textoO3 = "O3"
        const tipoO3 = `${textoO3}`;
        // Añade el marcador a la capa correspondiente
        capas[tipoO3].addLayer(idwO3);
    } else {
        console.log("No hay mediciones de Ozono")
    }

    if (puntosInterpolacionNO != 0) {
        var idwNO = L.idwLayer(puntosInterpolacionNO, {
            opacity: 0.3,
            maxZoom: 18,
            cellSize: 10,
            exp: 3,
            max: 250,
            gradient: { 0: 'green', 0.6: 'yellow', 1: 'red' }
        }).addTo(mymap);
        const textoNO = "NO"
        const tipoNO = `${textoNO}`;
        // Añade el marcador a la capa correspondiente
        capas[tipoNO].addLayer(idwNO);
    } else {
        console.log("No hay mediciones de Oxidos de nitrogeno")
    }

    if (puntosInterpolacionSO2 != 0) {
        var idwSO2 = L.idwLayer(puntosInterpolacionSO2, {
            opacity: 0.3,
            maxZoom: 18,
            cellSize: 10,
            exp: 3,
            max: 463,
            gradient: { 0: 'green', 0.24: 'yellow', 1: 'red' }
        }).addTo(mymap);
        const textoSO2 = "SO2"
        const tipoSO2 = `${textoSO2}`;
        // Añade el marcador a la capa correspondiente
        capas[tipoSO2].addLayer(idwSO2);
    } else {
        console.log("No hay mediciones de Dioxido de Azufre")
    }

    if (puntosInterpolacionCO != 0) {
        var idwCO = L.idwLayer(puntosInterpolacionCO, {
            opacity: 0.3,
            maxZoom: 18,
            cellSize: 10,
            exp: 3,
            max: 13,
            gradient: { 0: 'green', 0.54: 'yellow', 1: 'red' }
        }).addTo(mymap);
        const textoCO = "CO"
        const tipoCO = `${textoCO}`;
        // Añade el marcador a la capa correspondiente
        capas[tipoCO].addLayer(idwCO);
    } else {
        console.log("No hay mediciones de Monoxido de carbono")
    }

    if (puntosInterpolacionC6H6.length != 0) {
        var idwC6H6 = L.idwLayer(puntosInterpolacionC6H6, {
            opacity: 0.3,
            maxZoom: 18,
            cellSize: 10,
            exp: 3,
            max: 7,
            gradient: { 0: 'green', 0.43: 'yellow', 1: 'red' }
        }).addTo(mymap);
        const textoC6H6 = "C6H6"
        const tipoC6H6 = `${textoC6H6}`;
        // Añade el marcador a la capa correspondiente
        capas[tipoC6H6].addLayer(idwC6H6);
    } else {
        console.log("No hay mediciones de Benceno")
    }
}

// .......................................................
// .......................................................
// ..................PERSONALIZACION......................
// .......................................................
// .......................................................

// .......................................................
// Texto --> getCustomIcon() --> Icono_customizado
// .......................................................
function getCustomIcon(type) {
    const color = getColorMarcador(type);
    return L.divIcon({
        className: 'custom-icon',
        iconSize: [20, 20],
        iconAnchor: [10, 10],
        popupAnchor: [0, -10],
        html: `<div style="background-color: ${color}; width: 20px; height: 20px; border-radius: 50%;"></div>`
    });
}

// .......................................................
// Texto --> addToLegend()
// .......................................................
function addToLegend(type) {
    const legendContent = document.getElementById('zonaMapa2');
    const entry = document.createElement('div');
    entry.innerHTML = `<span class="legend-marker" style="background-color: ${getColor(type)};"></span>${type}`;
    legendContent.appendChild(entry);
}

// .......................................................
// Texto --> getColorMarcador() --> Texto
// .......................................................
function getColorMarcador(nivel) {
    // Asigna colores diferentes según el tipo de medición
    return nivel === 'Alto' ? 'red' : nivel === 'Medio' ? 'yellow' : nivel === 'Bajo' ? 'green' : 'blue';
}

// .......................................................
// Texto --> getColor() --> Texto
// .......................................................
function getColor(type) {
    // Asigna colores diferentes según el tipo de medición
    return type === 'CO' ? 'red' : type === 'O3' ? 'blue' : type === 'NO' ? 'yellow' : type === 'SO2' ? 'pink' : 'green';
}

// .......................................................
// Texto --> createLegend() --> Leyenda
// .......................................................
function createLegend(types) {
    const legend = L.control({ position: 'bottomright' });

    legend.onAdd = function (map) {
        const div = L.DomUtil.create('div', 'legend');
        // Genera las entradas de la leyenda según los tipos proporcionados
        div.innerHTML = '<h4>Leyenda</h4>' +
            types.map(type => `<div class="legend-item"><span class="circle" style="background-color: ${getColorMarcador(type)};"></span>${type}</div>`).join('');
        return div;
    };

    return legend;
}

// .......................................................
// Texto --> getColorByClasificacion() --> Texto
// .......................................................
function getColorByClasificacion(clasificacion) {
    // Define colores según la clasificación
    const colores = {
        Alto: 'red',
        Medio: 'yellow',
        Bajo: 'green'
    };
    return colores[clasificacion] || 'gray'; // Color por defecto
}

// .......................................................
// Texto --> getClasificacion() --> Texto
// .......................................................
function getClasificacion(valor) {
    const umbralAlto = 30;
    const umbralMedio = 15;
    if (valor > umbralAlto) {
        return 'Alto';
    } else if (valor > umbralMedio) {
        return 'Medio';
    } else {
        return 'Bajo';
    }
}

// .......................................................
// .......................................................
// .....................FILTROS...........................
// .......................................................
// .......................................................

// .......................................................
// filtrarPorFecha()
// .......................................................
function filtrarPorFecha() {
    const fechaInput = document.getElementById('fechaInput').value;

    //console.log("Fecha: " + fechaInput)

    convertirFecha(fechaInput)
    //getFechaHoy()
    //mostrarLeyendaYMarcadores(mymap, fechaInicio, fechaFin);
    //Falta ejecutar para crear el mapa, pero habria que modificar las cosas para que pase las fechas desde el principio
}

let fechaInicio;
let fechaFin;

// .......................................................
// Texto --> convertirFecha()
// .......................................................
function convertirFecha(fecha) {
    let fechaObj = new Date(fecha);
    let fechaNueva = fecha + ' 23:55:55'

    const año = fechaObj.getFullYear();
    const mes = (fechaObj.getMonth() + 1).toString().padStart(2, '0'); // Se suma 1 porque los meses comienzan desde 0
    const dia = fechaObj.getDate().toString().padStart(2, '0');
    const horas = fechaObj.getHours().toString().padStart(2, '0');
    const minutos = fechaObj.getMinutes().toString().padStart(2, '0');
    const segundos = fechaObj.getSeconds().toString().padStart(2, '0');

    let fechaNuevaDiaAntes = `${año}-${mes}-${dia} ${horas}:${minutos}:${segundos}`;
    //let fechaNuevaDiaAntes = (fecha - 1) + ' 23:55:55'
    //console.log("Fecha convertida: " + fechaNueva)
    //console.log("Fecha convertida dia antes: " + fechaNuevaDiaAntes)
    fechaInicio = fechaNuevaDiaAntes;
    fechaFin = fechaNueva;
    generarYGenerarMapa(fechaNuevaDiaAntes, fechaNueva, "Admin");
}

// .......................................................
// getFechaHoy() --> fechaHoy, fechaAyer
// .......................................................
function getFechaHoy() {
    // Obtener la fecha de hoy
    const fechaDeHoy = new Date();

    // Obtener componentes de fecha y hora
    const año = fechaDeHoy.getFullYear();
    const mes = (fechaDeHoy.getMonth() + 1).toString().padStart(2, '0'); // Se suma 1 porque los meses comienzan desde 0
    const dia = fechaDeHoy.getDate().toString().padStart(2, '0');
    const horas = fechaDeHoy.getHours().toString().padStart(2, '0');
    const minutos = fechaDeHoy.getMinutes().toString().padStart(2, '0');
    const segundos = fechaDeHoy.getSeconds().toString().padStart(2, '0');

    // Formatear la fecha en el formato deseado
    const fechaFormateadaHoy = `${año}-${mes}-${dia} ${horas}:${minutos}:${segundos}`;

    // Obtener la fecha de ayer
    const fechaDeAyer = new Date();
    fechaDeAyer.setDate(fechaDeAyer.getDate() - 1);

    // Obtener componentes de fecha y hora
    const año2 = fechaDeAyer.getFullYear();
    const mes2 = (fechaDeAyer.getMonth() + 1).toString().padStart(2, '0'); // Se suma 1 porque los meses comienzan desde 0
    const dia2 = fechaDeAyer.getDate().toString().padStart(2, '0');
    const horas2 = fechaDeAyer.getHours().toString().padStart(2, '0');
    const minutos2 = fechaDeAyer.getMinutes().toString().padStart(2, '0');
    const segundos2 = fechaDeAyer.getSeconds().toString().padStart(2, '0');

    // Formatear la fecha en el formato deseado
    const fechaFormateadaAyer = `${año2}-${mes2}-${dia2} ${horas2}:${minutos2}:${segundos2}`;

    return [fechaFormateadaHoy, fechaFormateadaAyer]
}

// .......................................................
// .......................................................
// ....................ESTACIONES.........................
// .......................................................
// .......................................................

// .......................................................
// Mapa --> obtenerEstacionMedidaOficial()
// .......................................................
function obtenerEstacionMedidaOficial(mymap) {
    fetch('http://192.168.43.252:8080/obtenerDatosAEMET2/', {
        method: "GET"
    }).then(function (respuesta) {

        if (respuesta.ok) {
            return respuesta.json()
        } else {
            console.log("hubo un fallo")
        }

    }).then(function (datos) {
        const datosJSON = JSON.stringify(datos);
        console.log(datosJSON);

        var datosSO2 = 0;
        var datosNO = 0;
        var datosNO2 = 0;
        var datosO3 = 0;
        var datosTemperatura = 0;
        var datosPM10 = 0;
        var contador = 0;

        datos.forEach(medicionEstacion => {
            if (medicionEstacion["SO2 en microgramos/m3"] != undefined) {
                datosSO2 = datosSO2 + parseFloat(medicionEstacion["SO2 en microgramos/m3"]);
                datosNO = datosNO + parseFloat(medicionEstacion["NO en microgramos/m3"]);
                datosNO2 = datosNO2 + parseFloat(medicionEstacion["NO2 en microgramos/m3"]);
                datosO3 = datosO3 + parseFloat(medicionEstacion["O3 en microgramos/m3"]);
                datosTemperatura = datosTemperatura + parseFloat(medicionEstacion["Temperatura en grados celsius"]);
                datosPM10 = datosPM10 + parseFloat(medicionEstacion["PM10 en microgramos/m3"]);
                contador++;
            }
        });

        var finalDatosSO2 = datosSO2 / contador;
        var finalDatosNO = datosNO / contador;
        var finalDatosNO2 = datosNO2 / contador;
        var finalDatosO3 = datosO3 / contador;
        var finalDatosTemperatura = datosTemperatura / contador;
        var finalDatosPM10 = datosPM10 / contador;
        console.log("EstacionesFinal. SO2: " + finalDatosSO2 + ". NO: " + finalDatosNO);

        //const tipo = `${markerData.tipo_valor}`;

        // Dividir la cadena 'lugar' en latitud y longitud
        //const [latitud, longitud] = markerData.lugar.split(',').map(parseFloat);
        const latitud = "39.091231";
        const longitud = "-1.074932";

        console.log("Lat: " + latitud + ", Lon: " + longitud);
        const marker = L.marker([latitud, longitud], { icon: getCustomIcon('') }).addTo(mymap);
        marker.bindPopup(`SO2: ${finalDatosSO2}<br>NO: ${finalDatosNO}<br>NO2: ${finalDatosNO2}<br>O3: ${finalDatosO3}<br>Temp: ${finalDatosTemperatura}<br>PM10: ${finalDatosPM10}`);
        //marker.setIcon(getCustomIcon(markerData.tipo_valor, 'red'));

        marcadores.push(marker)

        // Añade el marcador a la capa correspondiente
        //capas[tipo].addLayer(marker);
    });
}

// .......................................................
// .......................................................
// .....................DESCARGAS.........................
// .......................................................
// .......................................................

// .......................................................
// downloadToPNG()
// .......................................................
function downloadToPNG() {

    html2canvas(document.getElementById('zonaMapa2'), {
        useCORS: true, // Necesario para capturar mapas de azulejos externos
    }).then(function (canvas) {
        // Crea un enlace temporal y establece la imagen como su contenido
        var link = document.createElement('a');

        // Convierte el canvas a una URL de datos PNG
        link.href = canvas.toDataURL('image/png');

        // Establece el nombre del archivo
        link.download = 'mapa.png';

        // Simula el clic en el enlace para iniciar la descarga
        link.click();
    });
}

// .......................................................
// downloadToCSV()
// .......................................................
function downloadToCSV() {

    var csv = "Latitud;Longitud;Contaminante;Valor;Fecha\n";

    var contador = 0;
    // Agregar datos de marcadores al CSV
    marcadores.forEach(function (medicion) {
        var lat = medicion._latlng.lat;
        var lng = medicion._latlng.lng;
        var valor = datosMediciones[contador].valor;
        var fecha = datosMediciones[contador].fecha
        var tipoValor = datosMediciones[contador].tipo_valor_id
        var contaminante = "";
        switch (tipoValor) {
            case 0:
                contaminante = "Temperatura"
                break;
            case 1:
                contaminante = "Ozono-O3"
                break;
            case 2:
                contaminante = "Monoxido de nitrogeno-NO"
                break;
            case 3:
                contaminante = "Dioxido de Azufre-SO2"
                break;
            case 4:
                contaminante = "Monoxido de carbono-CO"
                break;

        }
        csv += lat + ';' + lng + ';' + contaminante + ';' + valor + ';' + fecha + '\n';
        contador++
    });


    // Crea un blob con el contenido CSV
    var blob = new Blob([csv], { type: 'text/csv;charset=utf-8' });

    // Crea un enlace temporal y establece el blob como su contenido
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);

    // Establece el nombre del archivo CSV
    link.download = 'datos_mapa.csv';

    // Simula el clic en el enlace para iniciar la descarga
    link.click();
}

// .......................................................
// downloadToExcel()
// .......................................................
function downloadToExcel() {

    var excelHeaders = ["Latitud", "Longitud", "Contaminante", "Valor", "Fecha"];

    var contador = 0;

    var excelData = []
    marcadores.forEach(function (medicion) {
        var lat = medicion._latlng.lat;
        var lng = medicion._latlng.lng;
        var valor = datosMediciones[contador].valor;
        var fecha = datosMediciones[contador].fecha
        var tipoValor = datosMediciones[contador].tipo_valor_id
        var contaminante = "";
        switch (tipoValor) {
            case 0:
                contaminante = "Temperatura"
                break;
            case 1:
                contaminante = "Ozono-O3"
                break;
            case 2:
                contaminante = "Monoxido de nitrogeno-NO"
                break;
            case 3:
                contaminante = "Dioxido de Azufre-SO2"
                break;
            case 4:
                contaminante = "Monoxido de carbono-CO"
                break;

        }
        excelData.push([lat, lng, contaminante, valor, fecha])
        contador++
    });


    // Crear un nuevo libro de Excel
    var workbook = XLSX.utils.book_new();
    var worksheet = XLSX.utils.aoa_to_sheet([excelHeaders].concat(excelData));

    // Agregar la hoja al libro
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Datos del Mapa');

    // Crear un blob con el contenido Excel
    var excelBinaryData = XLSX.write(workbook, { bookType: 'xlsx', type: 'binary' });
    var blob = new Blob([s2ab(excelBinaryData)], { type: 'application/octet-stream' });

    // Crear un enlace temporal y establecer el blob como su contenido
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);

    // Establecer el nombre del archivo Excel
    link.download = 'datos_mapa.xlsx';
    // Simular el clic en el enlace para iniciar la descarga
    link.click();
}

// .......................................................
// s --> s2ab() --> buf
// .......................................................
function s2ab(s) {
    var buf = new ArrayBuffer(s.length);
    var view = new Uint8Array(buf);
    for (var i = 0; i < s.length; i++) view[i] = s.charCodeAt(i) & 0xFF;
    return buf;
}

// .......................................................
// downloadToJSON()
// .......................................................
function downloadToJSON() {

    var contador = 0;

    var JSONData = []
    marcadores.forEach(function (medicion) {
        var lat = medicion._latlng.lat;
        var lng = medicion._latlng.lng;
        var valor = datosMediciones[contador].valor;
        var fecha = datosMediciones[contador].fecha
        var tipoValor = datosMediciones[contador].tipo_valor_id
        var contaminante = "";
        switch (tipoValor) {
            case 0:
                contaminante = "Temperatura"
                break;
            case 1:
                contaminante = "Ozono-O3"
                break;
            case 2:
                contaminante = "Monoxido de nitrogeno-NO"
                break;
            case 3:
                contaminante = "Dioxido de Azufre-SO2"
                break;
            case 4:
                contaminante = "Monoxido de carbono-CO"
                break;

        }
        JSONData.push({ "latitud": lat, "longitud": lng, "contaminante": contaminante, "valor": valor, "fecha": fecha })
        contador++
    });

    var jsonString = JSON.stringify(JSONData, null, 2);

    // Crear un blob con el contenido JSON
    var blob = new Blob([jsonString], { type: 'application/json' });

    // Crear un enlace temporal y establecer el blob como su contenido
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);

    // Establecer el nombre del archivo JSON
    link.download = 'datos_mapa.json';

    // Simular el clic en el enlace para iniciar la descarga
    link.click();
}

// .......................................................
// downloadToGeoJSON()
// .......................................................
function downloadToGeoJSON() {

    var contador = 0;

    var GeoJSONFeatures = []
    marcadores.forEach(function (medicion) {
        var lat = medicion._latlng.lat;
        var lng = medicion._latlng.lng;
        var valor = datosMediciones[contador].valor;
        var fecha = datosMediciones[contador].fecha
        var tipoValor = datosMediciones[contador].tipo_valor_id
        var contaminante = "";
        switch (tipoValor) {
            case 0:
                contaminante = "Temperatura"
                break;
            case 1:
                contaminante = "Ozono-O3"
                break;
            case 2:
                contaminante = "Monoxido de nitrogeno-NO"
                break;
            case 3:
                contaminante = "Dioxido de Azufre-SO2"
                break;
            case 4:
                contaminante = "Monoxido de carbono-CO"
                break;

        }

        GeoJSONFeatures.push({
            type: "Feature",
            geometry: {
                type: "Point",
                coordinates: [lat, lng]
            },
            properties: {
                tipoContaminante: contaminante,
                valorContaminante: valor,
                fecha: fecha
            }
        })
        contador++
    });

    var geoJSONData = {
        type: "FeatureCollection",
        features: GeoJSONFeatures
    }

    var geojsonString = JSON.stringify(geoJSONData, null, 2);

    // Crear un blob con el contenido GeoJSON
    var blob = new Blob([geojsonString], { type: 'application/json' });

    // Crear un enlace temporal y establecer el blob como su contenido
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);

    // Establecer el nombre del archivo GeoJSON
    link.download = 'datos_mapa.geojson';

    // Simular el clic en el enlace para iniciar la descarga
    link.click();
}

// .......................................................
// .......................................................
// ....................EJECUCION..........................
// .......................................................
// .......................................................

var datosMediciones = [];
var marcadores = [];

//const fechaInicio = '2023-10-14 16:32:40'
//const fechaFin = '2023-10-16 16:32:40'
/*const [fechaHoyFuera, fechaAyerFuera] = getFechaHoy()

fechaInicio = fechaAyerFuera;
fechaFin = fechaHoyFuera;

generarYGenerarMapa(fechaInicio, fechaFin);
setInterval(function () {
    // Aquí llamas a tu función con los argumentos necesarios
    generarYGenerarMapa(fechaInicio, fechaFin);
}, 50000);*/
//generarYGenerarMapa();