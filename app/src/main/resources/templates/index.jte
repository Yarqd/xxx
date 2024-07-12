@import java.lang.String
@param String flash
@param String flashType

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Добавление URL</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h1>Добавление URL</h1>
    @if(flash != null)
        <div class="alert @if(flashType != null && flashType.equals("error")) alert-danger @elseif(flashType != null && flashType.equals("success")) alert-success @endif" role="alert">
            ${flash}
        </div>
    @endif
    <form action="/urls" method="post">
        <div class="form-group">
            <label for="url">URL:</label>
            <input type="text" class="form-control" id="url" name="url" required>
        </div>
        <button type="submit" class="btn btn-primary">Добавить</button>
    </form>
</div>
</body>
</html>
