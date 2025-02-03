 function renderData(data){
    const dataContainer = document.getElementById('dataContainer');

    if( data && data.length > 0){
        data.forEach(item => {
            const dataItem = document.createElement('div');
            dataItem.textContent = `${item.id}: ${item.name}, ${item.type}, ${item.price} `;
            dataContainer.appendChild(dataItem);
        });

    } else {
        dataContainer.textContent = 'No data available';
    }
}
fetch('http://localhost:8080/api/items')
    .then(response => response.json())
    .then(data => renderData(data))
    .catch(error => {
        console.log('Error fetching data: ', error);
        const  dataContainer = document.getElementById('dataContainer');
        dataContainer.textContent = 'Error fetching data.';
    });