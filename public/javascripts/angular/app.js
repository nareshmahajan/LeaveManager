'use strict';

/** 
 * Declare's application level module which depends on directives, controllers, filters and services 
*/
var app = angular.module('leaveManagerApp', [ 'ui.router','ui.bootstrap','leaveManagerApp.filters',
    'leaveManagerApp.directives', 'leaveManagerApp.controllers','leaveManagerApp.services',
    'smart-table','daterangepicker','am.multiselect','toaster','ngAnimate','ui.calendar']);

/**
 * Application route configuration
 */
app.config(function($stateProvider, $urlRouterProvider) {
    //
    // For any unmatched url, redirect to /state1
       //
    $urlRouterProvider.otherwise('/defaultDashboard');
    $stateProvider
        .state('dashBoard', {
           templateUrl: "assets/partials/dashBoard.html",
            controller : 'dashboardController'
        })
        .state('dashBoard.applyLeave', {
            url :'/defaultDashboard',
            templateUrl: "assets/partials/applyLeave.html",
            controller: 'applyLeavesController'
        })
        .state('dashBoard.myLeaves', {
            templateUrl: "assets/partials/myLeaves.html",
            controller: 'MyLeavesController'
        })
        .state('searchLeaves', {
            templateUrl: "assets/partials/searchLeaves.html",
            controller: 'SearchLeavesService'
        })
        .state('searchLeaves.list', {
            templateUrl: "assets/partials/searchLeaveList.html",
            controller: 'SearchLeaveListController'
        })
        .state('searchLeaves.calendarView', {
            templateUrl: "assets/partials/calendar.html",
            controller: 'CalendarViewController'
        })
        .state('searchLeaves.approveLeaves', {
            templateUrl: "assets/partials/searchLeaveList.html",
            controller: 'ApproveLeavesController'
        })
        .state('cancelLeaves', {
            templateUrl: "assets/partials/cancelLeaves.html"
        })
        .state('cancelLeaves.myLeaves', {
            templateUrl: "assets/partials/myLeaves.html",
            controller: 'CancelLeavesController'
        })

        .state('holiday', {
            templateUrl: "assets/partials/holidayList.html",
            controller: 'HolidayController'
        })
        .state('onBehalfLeaves', {
            templateUrl: "assets/partials/onBehalfLeaves.html",
            controller: 'OnBehalfLeavesController'
        })


});


app.config(function ($httpProvider, $provide) {
    $provide.factory('myHttpInterceptor', function ($q, $location, $injector) {
        return {
            'response': function (response) {
                //you can handle you sucess response here.
                return response;
            },
            'responseError': function (rejection) {

              //  $state.go("sessionExpired",{}, {reload: true, inherit: false});
                    $('body').html(rejection.data);

            }
        };
    });
    $httpProvider.interceptors.push('myHttpInterceptor');
});

