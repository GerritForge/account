// Copyright (C) 2018 GerritForge Ltd
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

var app = angular.module('Account', [])

app.controller('AccountDeleteController', function AccountDeleteController($scope, $http, $window) {

  
  $scope.account = {
    "fullname" : "",
    "username" : "",
    "emails" : [ ]
  };
  
  $scope.fullName = "";
  
  $scope.alert = "";
  
  $scope.deleted = "";
  
  $http.delete('/a/accounts/self', {
    headers: {'X-Requested-With' : 'XMLHttpRequest'}
  }).then(function(response) {
    $scope.account = response.data.account_info;
  }, function(error) {
    $window.location.href = "/login";
  });
  
  $scope.backToGerrit = function() {
    if($scope.deleted) {
      $window.location.href = "/";
    }
  }
  
  $scope.deleteAccount = function() {
    $scope.alert = "";
    $scope.deleted = "";

    $http({
        method: 'DELETE',
        url: '/a/accounts/self',
        data: {
            account_name: $scope.fullName
        },
        headers: {
            'Content-type': 'application/json;charset=utf-8'
        }
    })
    .then(function(response) {
        if(!response.data.deleted) {
          $scope.alert = "Oops, something went wrong. Your full name does not match your profile (" + response.data.account_info.fullname + "). Please double-check and try again."
        } else {
          $http.get('/logout').then(function(response) {
            $scope.deleted = "Your account has been deletedfuly removed and you have been logged out from Gerrit Code Review";
          });
        }
    }, function(rejection) {
        $scope.alert = "Request failed: " + rejection.data;
    });
  }
});