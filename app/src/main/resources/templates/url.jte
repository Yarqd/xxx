@import hexlet.code.model.Url
@param Url url

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Детали URL</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h1>Детали URL</h1>
    <p>ID: ${url.getId()}</p>
    <p>URL: ${url.getName()}</p>
    <a href="/urls" class="btn btn-secondary mt-4">Назад</a>
</div>
</body>
</html>
