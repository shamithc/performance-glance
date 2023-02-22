'use strict';
angular.module('todo')
    .controller('controller', ['$scope', '$location', 'service', function ($scope, $location, service) {
        $scope.error = '';
        $scope.loadingMessage = '';
        $scope.todoList = null;
        $scope.editingInProgress = false;
        $scope.new = '';

        $scope.editTodo = {
            content: '',
            id: 0,
            finish: false
        };

        $scope.finishSwitch = function (todo) {
            service.putItem(todo).error(function (err) {
                todo.finished = !todo.finished;
                $scope.error = err;
                $scope.loadingMessage = '';
            })
        };

        $scope.editSwitch = function (todo) {
            todo.edit = !todo.edit;
            if (todo.edit) {
                $scope.editTodo.content = todo.content;
                $scope.editTodo.id = todo.id;
                $scope.editTodo.finished = todo.finished;
                $scope.editingInProgress = true;
            } else {
                $scope.editingInProgress = false;
            }
        };

        $scope.populate = function () {
            service.getItems().success(function (results) {
                $scope.todoList = results;
            }).error(function (err) {
                $scope.error = err;
                $scope.loadingMessage = '';
            })
        };

        $scope.delete = function (id) {
            service.deleteItem(id).success(function (results) {
                $scope.populate();
                $scope.loadingMessage = results;
                $scope.error = '';
            }).error(function (err) {
                $scope.error = err;
                $scope.loadingMessage = '';
            })
        };

        $scope.update = function (todo) {
            service.putItem($scope.editTodo).success(function (results) {
                $scope.populate();
                $scope.editSwitch(todo);
                $scope.loadingMessage = results;
                $scope.error = '';
            }).error(function (err) {
                $scope.error = err;
                $scope.loadingMessage = '';
            })
        };
        
        $scope.add = function () {
            function getUser() {
                var user = localStorage.getItem('user') || 'unknown';
                localStorage.setItem('user', user);
                return user;
            }

            service.postItem({
                'content': $scope.new,
                'owner': getUser(),
                'finish': 'false'
            }).success(function (results) {
                $scope.newTodoCaption = '';
                $scope.populate();
                $scope.loadingMessage = results;
                $scope.error = '';
            }).error(function (err) {
                $scope.error = err;
                $scope.loadingMsg = '';
            })
        };
    }]);
