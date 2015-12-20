'use strict';

/** 
 * Module listing all application Controllers 
 */
angular.module('leaveManagerApp.controllers', []).

    controller('dashboardController',
    [ '$scope', '$location', '$state','leaveCountService', function($scope, $location, $state, leaveCountService) {

        $scope.leaveCountInfo = leaveCountService.query();
        $scope.isActiveBreadCrumb = false;
        $scope.labels = {};
        $scope.labels.homeText = "Dashboard";
       // $scope.leavesTaken = 5;
       // $scope.leavesPlanned = 10;
       // $scope.leavesAvailable = 6;

    }])

    .controller('MainController',
    [ '$scope', '$location', '$anchorScroll','holidayService','$http', function($scope, $location, $anchorScroll, holidayService, $http) {

        $scope.toaster ={};
        $scope.menuList = [
            {"name" :"Dashboard","target":"dashBoard.applyLeave","class" : "active"},
            {"name" : "My Leaves","target":"dashBoard.myLeaves","class" :""},
            {"name" :"Cancel Leaves", "target":"cancelLeaves.myLeaves", "class" :""},
            {"name" :"Search Leaves", "target":"searchLeaves.list", "class" :""}];

        if(isApprover == 'Y'){
            $scope.menuList.push( {"name" :"Approve Leaves", "target":"searchLeaves.approveLeaves", "class" :""});
            $scope.menuList.push( {"name" :"On Behalf Leave", "target":"onBehalfLeaves", "class" :""});
        }
       // $scope.menuList.push( {"name" :"Calendar View", "target":"calendarView", "class" :""});
        if(isAdminUser == 'Y'){
            $scope.menuList.push( {"name" :"Holiday", "target":"holiday", "class" :""});
        }
        $scope.selectedMenu = $scope.menuList[0];

        $scope.toggleMenu = function(menuObj)
        {
            $scope.selectedMenu.class = "";
            menuObj.class = "active";
            $scope.selectedMenu = menuObj;
        }

        $scope.holidayListArray = {};
        $scope.holidayList = null;
        $http.post("holidayList").success( function(response) {
            $scope.holidayList = response;
            var log = [], dateObj, dateStr;
            angular.forEach($scope.holidayList, function(value, key) {
                dateObj = new Date(value.holidayDate);
                dateStr = dateObj.getYear()+"/"+dateObj.getMonth()+"/"+dateObj.getDate();
                $scope.holidayListArray[dateStr] = value.reason;
            }, log);
        });

        $scope.isHoliday = function(dateObj){
            var log = [];
            var strDate =  dateObj.getYear()+"/"+dateObj.getMonth()+"/"+dateObj.getDate();

            if($scope.holidayListArray[strDate] != null)
            {
                return true;
            }
            return false;
        };

        $scope.getHoliday = function(dateObj){
            var log = [];
            var strDate =  dateObj.getYear()+"/"+dateObj.getMonth()+"/"+dateObj.getDate();
            return $scope.holidayListArray[strDate];
        };

        $scope.gotoHome = function()
        {
            $scope.selectedMenu = $scope.menuList[0];
        }

    }])

    .controller('MyLeavesController',
    [ '$scope', '$location', 'leaveService','$state','$http', function($scope, $location, leaveService, $state, $http) {
        $scope.leaveInfo = {};
        $scope.totalItems = null;
        $scope.leaveInfo.currentPage = 1;
        $scope.maxSize = 5;
        $scope.leaveInfo.rowSize = 10;

        $scope.labels.isActiveBreadCrumb = true;
        $scope.labels.homeText = "Dashboard";
        $scope.labels.BreadCrumbText = "My Leaves";
        $scope.headerClass = {date: 'table-date-content', label:'fill-content', amount: 'table-amount-content'};
        $scope.contentClass = {date: 'bold', label:'grey', amount: 'grey'};

        $scope.headers = [
            {
                label: 'From',
                sortableField: true,
                contentField: 'fromDate',
                contentType: 'text',
                contentFilter: {
                    filter: 'date',
                    pattern : 'dd/MM/yyyy'
                }
            },{
                label: 'To',
                sortableField: true,
                contentField: 'toDate',
                contentType: 'text',
                contentFilter: {
                    filter: 'date',
                    pattern : 'dd/MM/yyyy'
                }
            },{
                label: 'Days',
                sortableField: true,
                contentField: 'leaveDays',
                contentType: 'text'
            },{
                label: 'Reason',
                sortableField: true,
                contentField: 'leaveReason',
                contentType: 'text'
            },{
                label: 'Status',
                sortableField: true,
                contentField: 'status',
                contentType: 'statusIcon'
            }

        ];

        $scope.searchLeaves = function()
        {
            $http.post("userLeavesOfCurrentYear",$scope.leaveInfo).success( function(response) {
                $scope.contents = response.leaves;
                $scope.totalItems = response.totalRows;
            });

            //$scope.contents = leaveService.searchLeaves($scope.leaveInfo);
        };

        $scope.cancelLeave = function(leaveId)
        {
            $http.post("cancelLeave/"+leaveId).success( function(response) {
                $state.go($state.current,{}, {reload: true, inherit: false});
            });

        };

        $scope.searchLeaves();
    }])

    .controller('CancelLeavesController',
    [ '$scope', '$location', 'leaveService', '$http', '$state',function($scope, $location, leaveService, $http, $state) {

        $scope.leaveInfo = {};
        $scope.totalItems = null;
        $scope.leaveInfo.currentPage = 1;
        $scope.maxSize = 5;
        $scope.leaveInfo.rowSize = 10;

        $scope.searchLeaves = function()
        {
            $http.post("userCancelLeavesOfCurrentYear",$scope.leaveInfo).success( function(response) {
                $scope.contents = response.leaves;
                $scope.totalItems = response.totalRows;
            });

            //$scope.contents = leaveService.searchLeaves($scope.leaveInfo);
        };

        $scope.headers = [
            {
                label: 'From',
                sortableField: true,
                contentField: 'fromDate',
                contentType: 'text',
                contentFilter: {
                    filter: 'date',
                    pattern : 'dd/MM/yyyy'
                }
            },{
                label: 'To',
                sortableField: true,
                contentField: 'toDate',
                contentType: 'text',
                contentFilter: {
                    filter: 'date',
                    pattern : 'dd/MM/yyyy'
                }
            },{
                label: 'Days',
                sortableField: true,
                contentField: 'leaveDays',
                contentType: 'text'
            },{
                label: 'Reason',
                sortableField: true,
                contentField: 'leaveReason',
                contentType: 'text'
            },{
                label: 'Status',
                sortableField: true,
                contentField: 'status',
                contentType: 'statusIcon'
            }

        ];

        $scope.cancelLeave = function(leaveId)
        {
            $http.post("cancelLeave/"+leaveId).success( function(response) {
                $state.go($state.current,{}, {reload: true, inherit: false});
            });

        };

        $scope.searchLeaves();
    }])

    .controller('applyLeavesController',
    [ '$scope', '$state', '$http','toaster','$filter', function($scope, $state, $http, toaster, $filter) {

        $scope.format = "dd/MM/yyyy";
        $scope.leaveInfo = {};
        $scope.fromDatePicker ={};
        $scope.toDatePicker ={};

        $scope.fromDatePicker.opened = false;
        $scope.toDatePicker.opened = false;
        $scope.fromDateOpen = function($event) {
            $scope.fromDatePicker.opened = true;
        };

        $scope.toDateOpen = function($event) {
            $scope.toDatePicker.opened = true;
        };

        $scope.applyLeave = function()
        {
            if($scope.isValid($scope.leaveInfo)) {
                var applyLeaveData = {};
                angular.copy($scope.leaveInfo, applyLeaveData);
                applyLeaveData.fromDate =  $filter('date')($scope.leaveInfo.fromDate, 'yyyy-MM-dd');
                applyLeaveData.toDate =  $filter('date')($scope.leaveInfo.toDate, 'yyyy-MM-dd');
                $http.post("applyLeave", applyLeaveData).success(function (response) {
                    var actionResult = response;
                    if(actionResult.status == 'SUCCESS') {
                        $scope.showSuccessMsg(applyLeaveData);
                        $scope.leaveInfo = {};
                        applyLeaveData = {};
                        $state.go($state.current,{}, {reload: true, inherit: false});
                    }
                    else if(actionResult.status == 'FAILED'){
                        $scope.showErrorMsg(actionResult.errors);
                    }

                }).error(function (response, jqXHR) {

                });
            }
        };

        $scope.showSuccessMsg = function(leaveObj){
            toaster.clear();
            toaster.pop('success', "Leaves Applied Successfully", "Your leave application for "+leaveObj.leaveReason+" " +
            "from "+leaveObj.fromDate+ " till "+leaveObj.toDate+" has been applied successfully.");
        };

        $scope.showErrorMsg = function(errors){

            toaster.clear();
            $scope.toaster.errors = {"errorList" : errors};

            toaster.pop('error', "Errors ", "{template: 'assets/partials/errors.html', data: toaster.errors}", 15000, 'templateWithData');
          //  toaster.pop('error', "Errors ", "{template: 'assets/partials/errors.html', data: bar}", 0, 'templateWithData');

        };

       $scope.isValid = function(leaveObj)
       {
            return true;
       };

        $scope.disabled = function(date, mode) {
            return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 )) || ($scope.isHoliday(date));
        };

        $scope.customCssClass = function(date, mode) {
            if ($scope.isHoliday(date)){
                return 'btn-danger';
            }
        };

    }])
    .controller('SearchLeavesService',
    [ '$scope', '$rootScope', '$http','$timeout','leaveService', function($scope, $rootScope, $http, $timeout, leaveService) {

        $scope.gridShow = false;
        $scope.leaveInfo = {};
        $scope.datePicker = {};
        $scope.isSearchLeaves = true;
        $scope.headerText = "Search Leaves";
        $scope.totalItems = null;
        $scope.leaveInfo.currentPage = 1;
        $scope.maxSize = 5;
        $scope.leaveInfo.rowSize = 10;
        $scope.selectedSearchView = 'LISTVIEW';
        //$scope.leaveInfo.dateRange = {};

        $scope.exists = function (item, list) {
            return list.indexOf(item) > -1;
        };

        $scope.loadEmployees = function()
        {
            //console.log($scope.leaveInfo.department);
            $http.post("departmentEmployees", $scope.leaveInfo.department).success( function(response) {
                $scope.employess = response;
                // unblockUI();
            });
        };

        $scope.loadGroups = function()
        {
            $http.post("departments").success( function(response) {
                $scope.groups = response;
                // unblockUI();
            });
        };


        $scope.loadGroups();


        $scope.activeView = function(view){
            $scope.selectedSearchView = view;
        };


        $scope.searchLeaves = function()
        {
            if($scope.selectedSearchView == 'LISTVIEW'){
                $rootScope.$broadcast('searchLeaveListView', $scope.leaveInfo );
            }
            else{
                $rootScope.$broadcast('refreshCalendarView');
            }
        };


      }])

    .controller('SearchLeaveListController',
    [ '$scope', '$location', '$http','$timeout','leaveService', function($scope, $location, $http, $timeout, leaveService) {

        $scope.gridShow = false;

        $scope.datePicker = {};
        $scope.isSearchLeaves = true;
        $scope.headerText = "Search Leaves";
        $scope.totalItems = null;
        $scope.leaveInfo.currentPage = 1;
        $scope.maxSize = 5;
        $scope.leaveInfo.rowSize = 10;

        //$scope.leaveInfo.dateRange = {};

        $scope.$on('searchLeaveListView', function() {
            $scope.searchLeaves();
        });

        $scope.searchLeaves = function()
        {
            $http.post("searchLeaves",$scope.leaveInfo).success( function(response) {
                $scope.contents = response.leaves;
                $scope.totalItems = response.totalRows;
            });

            //$scope.contents = leaveService.searchLeaves($scope.leaveInfo);
        };

        $scope.searchLeaves();

        $scope.headers = [{
            label: 'Employee',
            sortableField: true,
            contentField: 'employeeid',
            contentType: 'text'
        },
            {
                label: 'From',
                sortableField: true,
                contentField: 'fromDate',
                contentType: 'text',
                contentFilter: {
                    filter: 'date',
                    pattern : 'dd/MM/yyyy'
                }
            },{
                label: 'To',
                sortableField: true,
                contentField: 'toDate',
                contentType: 'text',
                contentFilter: {
                    filter: 'date',
                    pattern : 'dd/MM/yyyy'
                }
            },{
                label: 'Days',
                sortableField: true,
                contentField: 'leaveDays',
                contentType: 'text'
            },{
                label: 'Reason',
                sortableField: true,
                contentField: 'leaveReason',
                contentType: 'text'
            },{
                label: 'Status',
                sortableField: true,
                contentField: 'status',
                contentType: 'statusIcon'
            }

        ];

        $scope.pageChange = function(){
            $scope.searchLeaves();
        };

    }])

    .controller('ApproveLeavesController',
    [ '$scope', '$location', '$http','$timeout','leaveService','$state', function($scope, $location, $http, $timeout, leaveService, $state) {

        $scope.leaveInfo = {};
        $scope.datePicker = {};
        $scope.isApproveLeaves = true;
        $scope.headerText = "Approve Leaves";

        $scope.totalItems = null;
        $scope.leaveInfo.currentPage = 1;
        $scope.maxSize = 5;
        $scope.leaveInfo.rowSize = 10;

        //$scope.leaveInfo.dateRange = {};

        $scope.groups = [{"id":"FUNDTECH","name" : "Fundtech"},
            {"id":"FIRESTART","name" : "Firestart"},{"id":"LIQUIDICE","name" : "Liquidice"}]



        $scope.exists = function (item, list) {
            return list.indexOf(item) > -1;
        };

        $scope.loadEmployees = function()
        {
            //console.log($scope.leaveInfo.department);
            $http.post("departmentEmployees", $scope.leaveInfo.department).success( function(response) {
                $scope.employess = response;
                // unblockUI();
            });
        };

        $scope.loadGroups = function()
        {
            $http.post("departments").success( function(response) {
                $scope.groups = response;
                // unblockUI();
            });
        };

        $scope.searchLeaves = function()
        {

            $http.post("searchLeavesForApprover",$scope.leaveInfo).success( function(response) {
                $scope.contents = response.leaves;
                $scope.totalItems = response.totalRows;
            });

            //$scope.contents = leaveService.searchLeaves($scope.leaveInfo);
        };

        $scope.loadGroups();

        $scope.searchLeaves();

        $scope.headers = [{
            label: 'Employee',
            sortableField: true,
            contentField: 'employeeid',
            contentType: 'text',
            width : 15
        },
            {
                label: 'From',
                sortableField: true,
                contentField: 'fromDate',
                contentType: 'text',
                width : 14
            },{
                label: 'To',
                sortableField: true,
                contentField: 'toDate',
                contentType: 'text',
                width : 14
            },{
                label: 'Days',
                sortableField: true,
                contentField: 'leaveDays',
                contentType: 'text',
                width : 10
            },{
                label: 'Reason',
                sortableField: true,
                contentField: 'leaveReason',
                contentType: 'text',
                width : 20
            },{
                label: 'Status',
                sortableField: true,
                contentField: 'status',
                contentType: 'statusIcon',
                width : 32
            }

        ];


        $scope.approveLeave = function(leaveId){
            $http.post("approveLeave/"+leaveId).success( function(response) {
                $state.go($state.current,{}, {reload: true, inherit: false});
            });
        };

        $scope.rejectLeave = function(leaveId){
            $http.post("rejectLeave/"+leaveId).success( function(response) {
                $state.go($state.current,{}, {reload: true, inherit: false});
            });
        };

    }])

    .controller('CalendarViewController',
    [ '$scope', '$location', '$http','uiCalendarConfig', function($scope, $location, $http, uiCalendarConfig) {
        $scope.calendarLoad = true;

        $scope.loadGroups();

        $scope.$on('refreshCalendarView', function() {
            uiCalendarConfig.calendars.calendar.fullCalendar('refetchEvents');
        });


        $scope.eventRender = function( event, element, view ) {
            element.attr({'tooltip': event.title,
                'tooltip-append-to-body': true});

        };

        $scope.dayRender = function (date, cell) {
            var holidayText = $scope.getHoliday(new Date(date));
            if(null != holidayText) {

                cell.css("background-color", "rgb(192, 57, 43)");
                cell.css("color", "white");
               // cell.css("color", "white");
               // cell.prepend($scope.getHoliday(new Date(date)));
                cell.attr("data-original-title", holidayText);
                cell.attr("data-placement","bottom");
                cell.attr("data-toggle","tooltip");

                $(cell).tooltip({
                    container : 'body'
                });
                var dateObj = new Date(date);
                var dateStr = dateObj.getFullYear()+"-"+(dateObj.getMonth()+1)+"-"+dateObj.getDate();
                $('td[data-date='+dateStr+']').attr("title", holidayText);
                $('td[data-date='+dateStr+']').css("color", "white");
                $('td[data-date='+dateStr+']').attr("data-placement", "auto");

                cell.prepend("Holiday");
                $('td[data-date='+dateStr+']').tooltip({
                    container : 'body'
                });

            }
        };



        $scope.uiConfig = {
            calendar:{
                weekends : true,
                eventRender: $scope.eventRender,
                dayRender : $scope.dayRender,
                eventLimit : true
            }
        };

        $scope.eventsF =  function(start, end, timezone, callback) {

            var data = {
                dateRange :{
                    startDate : start,
                    endDate : end
                    //startDate : $.fullCalendar.formatDate(start,'MM-dd-yyyy'),
                    // endDate : $.fullCalendar.formatDate(end,'MM-dd-yyyy')
                },
                employee : $scope.leaveInfo.employee,
                department: $scope.leaveInfo.department
            };
            var longDate = null;
            $http.post("calenderEvents",data).success( function(response) {
                var eventList = response.eventsList;
                var userList = response.userList;
                var eventColors = {};
                var colors =["rgb(39, 174, 96)","rgb(52, 152, 219)","rgb(13,34,82)"];
                var count = 0;
                $(userList).each(function() {
                    var userId = $(this).selector;
                    eventColors[userId] = colors[count++ % 3];

                });


                $(eventList).each(function() {
                    longDate = $(this).attr("start");
                    $(this).attr("start", new Date(longDate));
                    $(this).attr("allDay", true);
                    longDate = $(this).attr("end");
                    var d = new Date(longDate);
                    d.setHours(25);
                    d.setMinutes(59);
                    d.setSeconds(59);
                    $(this).attr("end", d);

                    $(this).attr("backgroundColor",eventColors[$(this).attr("title")]);
                });
                callback(eventList);
            });
        };

       $scope.eventSources = [$scope.eventsF];

    }])

    .controller('HolidayController',
    [ '$scope', '$location', '$http','$uibModal','$state','$filter','toaster',
        function($scope, $location, $http, $uibModal, $state, $filter, toaster) {

        $scope.saveBtnText = "Save Holiday";
        $scope.format = "dd/MM/yyyy";
        $scope.leaveInfo = {};

        $scope.datePicker = {};
        $scope.isApproveLeaves = true;
        $scope.headerText = "Approve Leaves";

        $scope.totalItems = null;
        //$scope.leaveInfo.currentPage = 1;
        $scope.maxSize = 5;
       // $scope.leaveInfo.rowSize = 10;
        $scope.disabled = false;

        $scope.fromDateOpen = function($event) {
            $scope.datePicker.opened = true;
        };

        $scope.searchHolidays = function()
        {

            $http.post("holidayList").success( function(response) {
                $scope.contents = response;

            });

            //$scope.contents = leaveService.searchLeaves($scope.leaveInfo);
        };

        $scope.addHoliday = function(){
            $scope.leaveInfo.holidayDate = null;
            $scope.leaveInfo.reason = null;
            $scope.leaveInfo.holidayId = null;
            $scope.showHolidayEntryModal(false);
            $scope.titleText = "Add Holiday";
        };

        $scope.viewHoliday = function(holidayObj){
            $scope.updateEntryInfo(holidayObj);
            $scope.showHolidayEntryModal(true);
            $scope.titleText = "View Holiday";
        };

        $scope.editHoliday = function(holidayObj){
            $scope.updateEntryInfo(holidayObj);
            $scope.showHolidayEntryModal(false);
            $scope.titleText = "Edit Holiday";
        };

         $scope.deleteHoliday = function(holidayObj){
            $scope.disabled = false;
            $http.post("removeHoliday/"+holidayObj.holidayId).success(function (response) {
                var actionResult = response;
                if(actionResult.status == 'SUCCESS') {
                    $scope.showHolidayRemoveSuccessMsg(holidayObj);
                    $scope.leaveInfo = {};

                    $scope.searchHolidays();
                }
                else if(actionResult.status == 'FAILED'){
                    $scope.showErrorMsg(actionResult.errors);
                }

            }).error(function (response, jqXHR) {

            });
        };

            $scope.showHolidayRemoveSuccessMsg = function(leaveObj){
                toaster.clear();
                toaster.pop('success', "Holiday Removed Successfully", "Holiday For "+leaveObj.reason+" " +
                "On "+leaveObj.holidayDate+ " has been Removed successfully.");
            };
        $scope.updateEntryInfo = function(holidayObj){
            $scope.leaveInfo.holidayDate = holidayObj.holidayDate;
            $scope.leaveInfo.reason = holidayObj.reason;
            $scope.leaveInfo.holidayId = holidayObj.holidayId;
        };



        $scope.searchHolidays();

        $scope.headers = [
            {
                label: 'Action',
                sortableField: true,
                width : 7
            },
            {
                label: 'Holiday Date',
                sortableField: true,
                contentField: 'holidayDate',
                contentType: 'text',
                width : 7
            },{
                label: 'Reason',
                sortableField: true,
                contentField: 'reason',
                contentType: 'text',
                width : 20
            }

        ];
        $scope.isValid = function(){
          return true;
        };



        $scope.showHolidayEntryModal = function (varDisabled) {
                $scope.disabled = varDisabled;

                var modalInstance = $uibModal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'assets/partials/holidayEntry.html',
                controller: 'HolidayEntryController',
                size: "md",
                scope : $scope,
                resolve: {
                    items: function () {
                        return $scope.items;
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {

            });
        };

        $scope.dateDisabled = function(date, mode) {
            return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 )) || ($scope.isHoliday(date));
        };

        $scope.customCssClass = function(date, mode) {
            if ($scope.isHoliday(date)){
                return 'btn-danger';
            }
        };


    }])

    .controller('HolidayEntryController',['$scope', '$uibModalInstance','$filter','$http','toaster',
        function($scope, $uibModalInstance, $filter, $http, toaster){
        $scope.saveHoliday = function()
        {
            if($scope.isValid($scope.leaveInfo)) {
                var holidayData = {};
                angular.copy($scope.leaveInfo, holidayData);
                holidayData.holidayDate =  $filter('date')($scope.leaveInfo.holidayDate, 'yyyy-MM-dd');
                var strUrl = "addHoliday";
                if($scope.leaveInfo.holidayId != null){
                    var strUrl = "updateHoliday";
                }
                $http.post(strUrl, holidayData).success(function (response) {
                    var actionResult = response;
                    if(actionResult.status == 'SUCCESS') {
                        $scope.showSuccessMsg(holidayData);
                        $scope.leaveInfo = {};
                        holidayData = {};
                        $scope.searchHolidays();
                    }
                    else if(actionResult.status == 'FAILED'){
                        $scope.showErrorMsg(actionResult.errors);
                    }

                }).error(function (response, jqXHR) {

                });
            }
        };

        $scope.showSuccessMsg = function(leaveObj){
            toaster.clear();
            toaster.pop('success', "Holiday Saved Successfully", "Holiday For "+leaveObj.reason+" " +
            "On "+leaveObj.holidayDate+ " has been Saved successfully.");
        };

        $scope.showHolidayRemoveSuccessMsg = function(leaveObj){
            toaster.clear();
            toaster.pop('success', "Holiday Removed Successfully", "Holiday For "+leaveObj.reason+" " +
            "On "+leaveObj.holidayDate+ " has been Removed successfully.");
        };

        $scope.showErrorMsg = function(errors){

            toaster.clear();
            $scope.toaster.errors = {"errorList" : errors};

            toaster.pop('error', "Errors ", "{template: 'assets/partials/errors.html', data: toaster.errors}", 15000, 'templateWithData');
            //  toaster.pop('error', "Errors ", "{template: 'assets/partials/errors.html', data: bar}", 0, 'templateWithData');

        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }])
    .controller('OnBehalfLeavesController',
    [ '$scope', '$location', '$http','$timeout','$state','$filter','toaster', function($scope, $location, $http, $timeout, $state, $filter, toaster) {
        $scope.format = "dd/MM/yyyy";
        $scope.leaveInfo = {};
        $scope.fromDatePicker ={};
        $scope.toDatePicker ={};

        $scope.fromDatePicker.opened = false;
        $scope.toDatePicker.opened = false;
        $scope.fromDateOpen = function($event) {
            $scope.fromDatePicker.opened = true;
        };

        $scope.toDateOpen = function($event) {
            $scope.toDatePicker.opened = true;
        };

        $scope.applyLeave = function()
        {
            if($scope.isValid($scope.leaveInfo)) {
                var applyLeaveData = {};
                angular.copy($scope.leaveInfo, applyLeaveData);
                applyLeaveData.fromDate =  $filter('date')($scope.leaveInfo.fromDate, 'yyyy-MM-dd');
                applyLeaveData.toDate =  $filter('date')($scope.leaveInfo.toDate, 'yyyy-MM-dd');
                $http.post("applyOnBehalfLeave", applyLeaveData).success(function (response) {
                    var actionResult = response;
                    if(actionResult.status == 'SUCCESS') {
                        $scope.showSuccessMsg(applyLeaveData);
                        $scope.leaveInfo = {};
                        applyLeaveData = {};
                    }
                    else if(actionResult.status == 'FAILED'){
                        $scope.showErrorMsg(actionResult.errors);
                    }

                }).error(function (response, jqXHR) {

                });
            }
        };

        $scope.loadOnBehalfUserList= function()
        {
            //console.log($scope.leaveInfo.department);
            $http.post("onBehalfUserList").success( function(response) {
                $scope.onBehalfEmployeeList = response;
                // unblockUI();
            });
        };

        $scope.showSuccessMsg = function(leaveObj){
            toaster.clear();
            toaster.pop('success', "Leaves Applied Successfully", "Your leave application for "+leaveObj.leaveReason+" " +
            "from "+leaveObj.fromDate+ " till "+leaveObj.toDate+" has been applied successfully.");
        };

        $scope.showErrorMsg = function(errors){

            toaster.clear();
            $scope.toaster.errors = {"errorList" : errors};

            toaster.pop('error', "Errors ", "{template: 'assets/partials/errors.html', data: toaster.errors}", 15000, 'templateWithData');
            //  toaster.pop('error', "Errors ", "{template: 'assets/partials/errors.html', data: bar}", 0, 'templateWithData');

        };

        $scope.isValid = function(leaveObj)
        {
            return true;
        };

        $scope.disabled = function(date, mode) {
            return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 )) || ($scope.isHoliday(date));
        };

        $scope.customCssClass = function(date, mode) {
            if ($scope.isHoliday(date)){
                return 'btn-danger';
            }
        };

        $scope.loadOnBehalfUserList();

    }])




