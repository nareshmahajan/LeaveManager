'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('leaveManagerApp.services', ['ngResource']).
   factory('holidayService', [ '$resource', function($resource){
        return $resource('/holidayList', {}, {
            query: {method:'POST',q: '*',isArray:true}
        });
    }])
    .service('leaveService', [ '$resource','$filter','$http', function($resource, $filter, $http){

        this.getMyLeaves = function () {
            return $resource('/userLeavesOfCurrentYear', {}, {
                query: {method: 'POST', q: '*', isArray: true}
            }).query();
        }

        this.searchLeaves = function (jsonData) {
            return $resource('/searchLeaves', jsonData, {
                query: {method: 'POST', q: '*', isArray: true}
            }).query();
        }

        this.getCancelLeaves = function () {
            var leaves =  $resource('/userCancelLeavesOfCurrentYear', {}, {
                query: {method: 'POST', q: '*', isArray: true}
            }).query();

            //leaves = $filter('filter')(leaves, {status:'Cancelled'});
            return leaves;
        }

        this.cancelLeave = function(leaveId){
            var result = false;

            return result;
        }

    }])
    .factory('leaveCountService', [ '$resource', function($resource){
        return $resource('/leavesCount', {}, {
            query: {method:'POST',q: '*',isArray:false}
        });
    }]);