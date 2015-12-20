'use strict';

/**
 * Module listing all application Directives
 */
angular.module('leaveManagerApp.directives', []).

/**
 * Tooltip directive used for showing validation errors on tooltip. As soon as input error gets
 * resolved tooltip is destroyed/removed
 */
    directive('rowColor', function() {
        return {
            'scope': false,
            'restrict' :'A',
            'link': function(scope, element, attrs) {
                var someFunc = function(status)
                {
                    var bgColorClass = '';
                    if(status.toUpperCase() == 'PENDING')
                    {
                        bgColorClass = 'row-status-pending';
                    }
                    else if(status.toUpperCase() == 'APPROVED')
                    {
                        bgColorClass = 'row-status-approved';
                    }
                    else if(status.toUpperCase() == 'REJECTED')
                    {
                        bgColorClass = 'row-status-rejected';
                    }
                    else if(status.toUpperCase() == 'CANCELLED')
                    {
                        bgColorClass = 'row-status-cancelled';
                    }
                    return bgColorClass;
                }
                var newStyle = attrs.rowColor;
                scope.$watch(newStyle, function (style) {
                    if (!style) {
                        return ;
                    }
                    attrs.$set('class', someFunc(style));
                });
            }
        };
    })
    .directive('calendarView', ['$parse', function($parse) {
        return {
            restrict: "E",
            replace: true,
            transclude: false,
            compile: function (element, attrs) {
                var modelAccessor = $parse(attrs.ngModel);

                var html = "<div>" +
                    "</div>";

                var newElem = $(html);
                element.replaceWith(newElem);

                return function (scope, element, attrs, controller) {
                    var calendarObj = null;
                    var processChange = function () {

                    };


                    scope.$watch(modelAccessor, function (val) {
                        console.log(val);
                        //calendarObj.events = val;

                        $(newElem).fullCalendar({
                                events:val
                            });
                    });

                };

            }
        };
     }]);
