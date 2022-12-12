function addInputLine() {
    var label = document.createElement("label");
    var input = document.createElement("input");                 // Create an <input> node

    label.setAttribute("id", "add-image-label");
    input.setAttribute("type", "file");
    input.setAttribute("th:field", "*{image}");

    document.getElementById("add-album-div").appendChild(label);// Append it to the parent
    document.getElementById("add-image-label").appendChild(input);
}