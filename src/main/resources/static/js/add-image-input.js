function addInputLine() {
    const input = document.createElement("input");// Create an <input> node

    input.setAttribute("type", "file");
    input.setAttribute("th:field", "*{image}");

    document.getElementById("add-album-div").appendChild(input);// Append it to the parent
}