<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Главная страница</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
</head>
<body>
<div class="container">
    <h1>Добавить URL</h1>
    <form action="/urls" method="post">
        <div class="form-group">
            <label for="url">URL</label>
            <input type="text" class="form-control" id="url" name="url" required>
        </div>
        <button type="submit" class="btn btn-primary">Добавить</button>
    </form>
    <a href="/urls" class="btn btn-secondary mt-3">Список URL</a>
    @if(flashMessage != null)
        <div class="alert alert-info mt-3">
            @flashMessage
        </div>
    @endif
</div>
</body>
</html>
